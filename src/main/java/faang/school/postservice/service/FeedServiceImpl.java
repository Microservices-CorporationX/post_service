package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.redis.mapper.AuthorCacheMapper;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import faang.school.postservice.redis.model.dto.AuthorRedisDto;
import faang.school.postservice.redis.model.dto.FeedDto;
import faang.school.postservice.redis.model.dto.PostRedisDto;
import faang.school.postservice.redis.model.entity.AuthorCache;
import faang.school.postservice.redis.model.entity.FeedCache;
import faang.school.postservice.redis.model.entity.PostCache;
import faang.school.postservice.redis.repository.AuthorCacheRedisRepository;
import faang.school.postservice.redis.repository.FeedsCacheRepository;
import faang.school.postservice.redis.repository.PostCacheRedisRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final FeedsCacheRepository feedsCacheRepository;
    private final PostCacheRedisRepository postCacheRedisRepository;
    private final AuthorCacheRedisRepository authorCacheRedisRepository;
    private final PostRepository postRepository;
    private final PostCacheMapper postCacheMapper;
    private final AuthorCacheMapper authorCacheMapper;
    private final UserServiceClient userServiceClient;

    @Value(value = "${feed-posts.size}")
    private int postsSize;

    @Value(value = "${feed-posts-per-request.size}")
    private int postsPerRequest;

    @Override
    public FeedDto getFeed(Long feedId, Long userId, Integer startPostId) {
        startPostId = (startPostId == null) ? 0 : startPostId;
        FeedCache requestedFeed;
        try {
            requestedFeed = feedsCacheRepository.findById(feedId).orElseThrow(() -> {
                log.info("Feed with id {} not found in Redis", feedId);
                return new NoSuchElementException("Feed with id " + feedId + " not found");
            });
        } catch (NoSuchElementException e) {
            log.info("Try to make feed with id {} from Postgres", feedId);
            return getFeedFromPostgres(feedId);
        }
        List<Long> sublist = getSubList(requestedFeed.getPostIds(), startPostId, postsPerRequest);
        List<PostCache> feedPosts = getPostsFromCache(sublist);

        Set<Long> authorIds = feedPosts.stream().map(PostCache::getAuthorId).collect(Collectors.toSet());
        Map<Long, AuthorCache> authorCaches = getAuthorsFromCache(authorIds);
        Map<Long, AuthorRedisDto> authorRedisDtos = new HashMap<>();
        authorCaches.forEach((key, value) ->
                authorRedisDtos.put(key, authorCacheMapper.toAuthorRedisDto(value)));

        TreeSet<PostRedisDto> postRedisDtos = feedPosts.stream().map(post -> {
            PostRedisDto postRedisDto = postCacheMapper.toPostRedisDto(post);
            postRedisDto.setAuthor(authorRedisDtos.get(post.getAuthorId()));
            return postRedisDto;
        }).collect(Collectors.toCollection(TreeSet::new));

        if (postRedisDtos.size() == sublist.size()) {
            return FeedDto.builder().postRedisDtos(postRedisDtos).id(requestedFeed.getId()).build();
        }
        log.info("Can't find posts in Redis, try to get posts from Postgres");
        List<Long> receivedPostIds = postRedisDtos.stream().map(PostRedisDto::getId).toList();
        sublist.removeAll(receivedPostIds);
        postRedisDtos.addAll(getFromPostgres(sublist));
        return FeedDto.builder().postRedisDtos(postRedisDtos).id(requestedFeed.getId()).build();
    }

    private List<PostCache> getPostsFromCache(List<Long> postIds) {
        return StreamSupport
                .stream(postCacheRedisRepository.findAllById(postIds).spliterator(), false)
                .toList();
    }

    private List<Long> getSubList(List<Long> originalList, int startIndex, int count) {
        if (originalList == null || originalList.size() < startIndex || count < 0) {
            throw new IllegalArgumentException("Invalid start index or count");
        }
        int endIndex = Math.min(startIndex + count, originalList.size());

        return originalList.subList(startIndex, endIndex);
    }

    private Map<Long, AuthorCache> getAuthorsFromCache(Set<Long> authorIds) {
        Map<Long, AuthorCache> authorCachesMap = new HashMap<>();
        List<AuthorCache> authorCaches = StreamSupport
                .stream(authorCacheRedisRepository.findAllById(authorIds).spliterator(), false)
                .toList();
        authorCaches.forEach(author -> authorCachesMap.put(author.getId(), author));

        return authorCachesMap;
    }

    private TreeSet<PostRedisDto> getFromPostgres(List<Long> postIds) {
        System.out.println("33333333"+postIds);
        List<Post> posts = postRepository.findAllById(postIds);
        if (posts.isEmpty()) {
            throw new NoSuchElementException("Posts not found in Postgres");
        }
        List<Long> authorIds = posts.stream().map(Post::getAuthorId).distinct().toList();
        List<AuthorRedisDto> authorRedisDtos = authorCacheMapper.toAuthorRedisDtos(userServiceClient.getUsersByIds(authorIds));

        return createFeedDto(posts, authorRedisDtos);
    }

    private FeedDto getFeedFromPostgres(Long feedId) {
        List<AuthorRedisDto> allAuthors = userServiceClient.getAllFollowing(feedId.toString());
        List<Long> allAuthorIds = allAuthors.stream().map(AuthorRedisDto::getId).toList();
        List<Post> allPosts = postRepository.findAllByAuthorIdIn(allAuthorIds, postsSize);
        allPosts.forEach(post -> System.out.println(post.getId() + " " + post.getAuthorId() + " " + post.getContent()));
        return FeedDto.builder().id(feedId).postRedisDtos(createFeedDto(allPosts, allAuthors))
                .build();
    }

    private TreeSet<PostRedisDto> createFeedDto(List<Post> posts, List<AuthorRedisDto> authors) {
        Map<Long, AuthorRedisDto> authorsMap = authors.stream()
                .collect(Collectors.toMap(AuthorRedisDto::getId, author -> author));
        return posts.stream().map(post -> {
            PostRedisDto postRedisDto = postCacheMapper.toPostRedisDto(post);
            postRedisDto.setAuthor(authorsMap.get(post.getAuthorId()));
            postRedisDto.getComments().forEach(comment -> comment.setPostId(post.getId()));
            return postRedisDto;
        }).collect(Collectors.toCollection(TreeSet::new));
    }
}
