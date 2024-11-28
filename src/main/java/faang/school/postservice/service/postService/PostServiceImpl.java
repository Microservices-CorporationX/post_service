package faang.school.postservice.service.postService;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.PostServiceValidator;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostServiceValidator validator;
    private final Comparator<Post> comparePostByCreateDateDesc = (post1, post2) -> post2.getCreatedAt()
            .compareTo(post1.getCreatedAt());
    private final Comparator<Post> comparePostByPublishDateDesc = (post1, post2) -> post2.getPublishedAt()
            .compareTo(post1.getPublishedAt());

    @Override
    public PostUpdateDto createDraft(PostCreateDto postCreateDto) {
        if(postCreateDto.projectId() != null) {
            validator.validateProjectId(postCreateDto.projectId());
        }
        if(postCreateDto.authorId() != null) {
            validator.validateAuthorId(postCreateDto.authorId());
        }

        Post savedPost = postRepository.save(postMapper.toEntity(postCreateDto));
        return postMapper.toDto(savedPost);
    }

    @Override
    public PostUpdateDto publicPost(long id) {
        Post post = getPost(id);
        validator.checkPublicationPost(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        Post updatedPost = postRepository.save(post);

        return postMapper.toDto(updatedPost);
    }

    @Override
    public PostUpdateDto updatePost(PostUpdateDto postUpdateDto) {
        Post oldPost = null;
        if(postUpdateDto.id() != null) {
            oldPost = getPost(postUpdateDto.id());
            validator.validateAuthorsEquals(oldPost, postUpdateDto);

            if(!oldPost.getContent().equals(postUpdateDto.content())) {
                oldPost.setContent(postUpdateDto.content());
                oldPost.setUpdatedAt(LocalDateTime.now());
                return postMapper.toDto(postRepository.save(oldPost));
            }
        }

        return postMapper.toDto(oldPost);
    }

    @Override
    public PostUpdateDto softDeletePost(long id) {
        Post post = getPost(id);

        if(!post.isDeleted()) {
            post.setDeleted(true);
            return postMapper.toDto(postRepository.save(post));
        }

        return postMapper.toDto(post);
    }

    @Override
    public PostUpdateDto getPostById(long id) {
        return postMapper.toDto(getPost(id));
    }

    @Override
    public List<PostUpdateDto> getPostDraftsByAuthorId(long id) {
        List<Post> allPosts = postRepository.findByAuthorId(id);

        return filerAndSortDraftPosts(allPosts);
    }

    @Override
    public List<PostUpdateDto> getPostDraftsByProjectId(long id) {
        List<Post> allPosts = postRepository.findByProjectId(id);

        return filerAndSortDraftPosts(allPosts);
    }

    @Override
    public List<PostUpdateDto> getPublishedPostsByAuthorId(long id) {
        List<Post> allPosts = postRepository.findByAuthorIdWithLikes(id);

        return filerAndSortPublishedPosts(allPosts);
    }

    @Override
    public List<PostUpdateDto> getPublishedPostsByProjectId(long id) {
        List<Post> allPosts = postRepository.findByProjectIdWithLikes(id);

        return filerAndSortPublishedPosts(allPosts);
    }

    private Post getPost(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Пост с id {" + id + "} не существует"));
    }

    private List<PostUpdateDto> filerAndSortDraftPosts(List<Post> posts) {
        return posts.stream()
                .filter(post -> Boolean.FALSE.equals(post.isPublished()) && Boolean.FALSE.equals(post.isDeleted()))
                .sorted(comparePostByCreateDateDesc)
                .map(postMapper::toDto)
                .toList();
    }

    private List<PostUpdateDto> filerAndSortPublishedPosts(List<Post> posts) {
        return posts.stream()
                .filter(post -> Boolean.TRUE.equals(post.isPublished()) && Boolean.FALSE.equals(post.isDeleted()))
                .sorted(comparePostByPublishDateDesc)
                .map(postMapper::toDto)
                .toList();
    }
}
