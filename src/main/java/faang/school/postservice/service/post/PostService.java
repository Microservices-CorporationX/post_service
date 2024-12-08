package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.BanUsersDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.user.UserBanPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostValidator postValidator;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserBanPublisher userBanPublisher;
    @Getter
    @Value("${banner.minimum-size-of-unverified-posts}")
    private int minimumSizeOfUnverifiedPosts;

    public Post findEntityById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Incorrect post id"));
    }

    public PostDto create(PostDto postDto) {
        postValidator.validateCreation(postDto);
        if (!Boolean.TRUE.equals(postDto.getPublished())) {
            postDto.setPublished(false);
        } else {
            postDto.setPublishedAt(LocalDateTime.now());
        }

        postDto.setCreatedAt(LocalDateTime.now());
        postDto.setUpdatedAt(LocalDateTime.now());

        Post post = postMapper.toEntity(postDto);
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto publish(long postId) {
        Post post = findEntityById(postId);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto update(PostDto postDto) {
        Post post = findEntityById(postDto.getId());
        postValidator.validateUpdate(post, postDto);

        post.setContent(postDto.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto deletePost(long id) {
        Post post = findEntityById(id);
        if (post.isDeleted()) {
            throw new DataValidationException("Post already deleted");
        }
        post.setPublished(false);
        post.setDeleted(true);
        post.setUpdatedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    public List<PostDto> getAllNonPublishedByAuthorId(long id) {
        postValidator.validateUser(id);
        return filterNonPublishedPostsByTimeToDto(postRepository.findByAuthorIdWithLikes(id));
    }

    public List<PostDto> getAllNonPublishedByProjectId(long id) {
        postValidator.validateProject(id);
        return filterNonPublishedPostsByTimeToDto(postRepository.findByProjectIdWithLikes(id));
    }

    public List<PostDto> getAllPublishedByAuthorId(long id) {
        postValidator.validateUser(id);
        return filterPublishedPostsByTimeToDto(postRepository.findByAuthorIdWithLikes(id));
    }

    public List<PostDto> getAllPublishedByProjectId(long id) {
        postValidator.validateProject(id);
        return filterPublishedPostsByTimeToDto(postRepository.findByProjectIdWithLikes(id));
    }


    private List<PostDto> filterPublishedPostsByTimeToDto(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    private List<PostDto> filterNonPublishedPostsByTimeToDto(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    public void banUsers() {
        List<Post> postsWithOffensiveContent = postRepository.findNotVerifiedPots()
                .orElseGet(() -> null);
        if (postsWithOffensiveContent == null) {
            log.info("users for ban not found! cause not posts which not verified");
            return;
        }
        List<Long> banningUsersIds = getBanningUsersIds(postsWithOffensiveContent);
        if (banningUsersIds.size() == 0) {
            log.info("users for ban not found!, " +
                    "cause no users who have unverified posts exceeding {}", minimumSizeOfUnverifiedPosts);
            return;
        }
        log.info("users for ban received! users ids: {}", banningUsersIds);
        userBanPublisher.publish(BanUsersDto
                                .builder()
                                .usersIds(banningUsersIds)
                                .build());
    }

    public List<Long> getBanningUsersIds(List<Post> posts) {
        return posts.stream()
                .map(Post::getAuthorId)
                .filter(authorId -> Collections.frequency(posts
                        .stream().map(Post::getAuthorId).toList(), authorId) >= minimumSizeOfUnverifiedPosts
                )
                .distinct()
                .toList();
    }
}
