package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.filter.PostFilters;
import faang.school.postservice.validator.post.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    private final PostMapper postMapper;
    private final PostValidator postValidator;
    private final List<PostFilters> postFilters;

    public PostResponseDto create(PostRequestDto postRequestDto) {
      //  postValidator.validateCreate(postRequestDto);

        Post post = postMapper.toEntity(postRequestDto);

        post.setPublished(false);
        post.setDeleted(false);
        Post savePost = postRepository.save(post);

        return  postMapper.toDto(savePost);
    }

    public PostResponseDto publishPost(Long id) {
        Post post = postValidator.validateAndGetPostById(id);
        postValidator.validatePublish(post);
        post.setPublished(true);
        post.setDeleted(false);

        return postMapper.toDto(postRepository.save(post));
    }

    public PostResponseDto updatePost(PostUpdateDto postDto) {
        Objects.requireNonNull(postDto, "PostUpdateDto cannot be null");

        Post post = postValidator.validateAndGetPostById(postDto.getId());
        post.setContent(postDto.getContent());
        return postMapper.toDto(postRepository.save(post));
    }

    public void deletePost(Long id) {
        Post post = postRepository
                .findById(id)
                .orElseThrow(EntityNotFoundException::new);
        postValidator.validateDelete(post);

        post.setPublished(false);
        post.setDeleted(true);
        postRepository.save(post);
    }

    public PostResponseDto getPostById(Long id) {
        return postRepository.findById(id)
                .map(postMapper::toDto)
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<PostResponseDto> getPosts(PostFilterDto filterDto) {
        Stream<Post> posts = StreamSupport.stream(postRepository.findAll().spliterator(), false);

        postFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .forEach(filter -> filter.apply(posts, filterDto));

        return postMapper.toDtoList(posts.toList());
    }
}
