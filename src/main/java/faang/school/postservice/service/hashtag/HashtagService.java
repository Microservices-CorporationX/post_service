package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.db_repository.HashTagRepository;
import faang.school.postservice.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashtagService {

    private final HashTagRepository hashTagRepository;
    private final PostValidator postValidator;
    private final PostMapper postMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "postsByHashtag", key = "#hashtag", unless = "#hashtag == null")
    public List<PostResponseDto> getPostsByHashtag(String hashtag) {
        List<Post> posts = hashTagRepository.findAllByHashtagTitle(hashtag);
        return postMapper.toListPostDto(posts);
    }

    @Transactional
    public void createHashtagToPost(String hashtagTitle, long postId, long userId) {
        Post post = postValidator.validateAndGetPostById(postId);
        postValidator.validateUserExist(userId);
        postValidator.validateUserToPost(post, userId);
        if (!hashtagTitle.startsWith("#")) {
            hashtagTitle = "#" + hashtagTitle;
        }
        Optional<Hashtag> hashtag = hashTagRepository.findByTitle(hashtagTitle);
        if (hashtag.isEmpty()) {
            Hashtag newHashtag = new Hashtag();
            newHashtag.setPosts(new ArrayList<>());
            newHashtag.setTitle(hashtagTitle);
            newHashtag.getPosts().add(post);
            hashTagRepository.save(newHashtag);
        } else {
            Hashtag hashtagFromRepository = hashtag.get();
            postValidator.validatePostHatThisHashtag(post, hashtagFromRepository);
            hashtagFromRepository.getPosts().add(post);
            hashTagRepository.save(hashtagFromRepository);
        }
    }

    @Transactional(readOnly = true)
    public List<String> getAllHashtagByPostId(long postId, long userId) {
        Post post = postValidator.validateAndGetPostById(postId);
        postValidator.validateUserExist(userId);
        postValidator.validateUserToPost(post, userId);
        return post.getHashtags().stream().map(Hashtag::getTitle).toList();
    }
}
