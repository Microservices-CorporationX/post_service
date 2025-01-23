package faang.school.postservice.service;

import faang.school.postservice.dto.posts.PostCreatingRequest;
import faang.school.postservice.dto.posts.PostResultResponse;
import faang.school.postservice.excpetions.PostWasNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.utils.PostUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class PostService {
    private final Logger logger = LoggerFactory.getLogger(PostService.class);
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final PostUtil postUtil;

    @Transactional
    public PostResultResponse createPost(PostCreatingRequest postCreatingDto) {
        logger.info("Creating the post with id : {}", postCreatingDto.id());
        Post post = Post.builder()
                .content(postCreatingDto.content())
                .published(false)
                .deleted(false)
                .build();

        logger.info("Validating the post creator with id : {}", postCreatingDto.id());
        int result = postUtil.validateCreator(postCreatingDto.authorId(), postCreatingDto.projectId());
        switch (result) {
            case 0 : post.setAuthorId(postCreatingDto.authorId());
            case 1 : post.setProjectId(postCreatingDto.projectId());
        }
        logger.info("Success validation for post : {}", postCreatingDto.id());

        post = postRepository.save(post);
        logger.info("Saved post with id : {}", postCreatingDto.id());

        return postMapper.toDto(post);
    }

    @Transactional
    public PostResultResponse publishPost(Long postId) {
        logger.info("Publishing post with id : {}", postId);
        postUtil.checkId(postId);
        Post post = findPostById(postId);
        if (post.isPublished()) {
            throw new IllegalArgumentException("Post already published!");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        post = postRepository.save(post);
        logger.info("Successfully published post with id : {}", postId);

        return postMapper.toDto(post);
    }

    @Transactional
    public PostResultResponse updatePost(Long postId, String updatingContent) {
        logger.info("Updating post with id : {}", postId);
        postUtil.checkId(postId);
        if (StringUtils.isBlank(updatingContent)) {
            throw new IllegalArgumentException("Updating content is blank!");
        }
        Post post = findPostById(postId);
        if (post.isDeleted() || !post.isPublished()) {
            throw new IllegalArgumentException("Post deleted or not published yet!");
        }
        post.setContent(updatingContent);

        postRepository.save(post);
        logger.info("Successfully updated post with id : {}", postId);

        return postMapper.toDto(post);
    }

    @Transactional
    public PostResultResponse softDelete(Long postId) {
        logger.info("Soft deleting post with id : {}", postId);
        postUtil.checkId(postId);
        Post post = findPostById(postId);
        logger.info("{}", post.isDeleted());
        if (post.isDeleted()) {
            throw new IllegalArgumentException("Post already marked as deleted!");
        }
        post.setDeleted(true);

        postRepository.save(post);
        logger.info("Successfully soft deleted post with id : {}", postId);
        return postMapper.toDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostResultResponse> getNoPublishedPostsByAuthor(Long authorId) {
        return getPostsByFilter(authorId, postRepository::findByAuthorId, post -> !post.isPublished());
    }

    @Transactional(readOnly = true)
    public List<PostResultResponse> getNoPublishedPostsByProject(Long projectId) {
        return getPostsByFilter(projectId, postRepository::findByProjectId, post -> !post.isPublished());
    }

    @Transactional(readOnly = true)
    public List<PostResultResponse> getPublishedPostsByAuthor(Long authorId) {
        return getPostsByFilter(authorId, postRepository::findByAuthorId, Post::isPublished);
    }

    @Transactional(readOnly = true)
    public List<PostResultResponse> getPublishedPostsByProject(Long projectId) {
        return getPostsByFilter(projectId, postRepository::findByProjectId, Post::isPublished);
    }

    public List<PostResultResponse> getPostsByFilter(Long id,
                                                     Function<Long, List<Post>> fetcher,
                                                     Predicate<Post> filter) {
        postUtil.checkId(id);
        return fetcher.apply(id)
                .stream()
                .filter(filter)
                .map(postMapper::toDto)
                .toList();
    }

    public Post findPostById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostWasNotFoundException("No posts was found!"));
    }
}
