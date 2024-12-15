package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostAuthorFilterDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.dto.resource.ResourceInfoDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.ResourceService;
import faang.school.postservice.service.image.ImageResizeService;
import faang.school.postservice.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    @Value("${app.posts.files.picture-max-width}")
    private int maxImageWidth;
    @Value("${app.posts.files.picture-max-height}")
    private int maxImageHeight;

    private final PostValidator postValidator;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final ResourceService resourceService;
    private final ImageResizeService imageResizeService;

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

    public List<PostDto> getPostsBy(PostAuthorFilterDto filter) {
        postValidator.validateFilter(filter);

        if (filter.isPublished() && filter.getAuthorId() != null) {
            return getAllPublishedByAuthorId(filter.getAuthorId());
        } else if (filter.isPublished() && filter.getProjectId() != null) {
            return getAllPublishedByProjectId(filter.getProjectId());
        } else if (!filter.isPublished() && filter.getAuthorId() != null) {
            return getAllNonPublishedByAuthorId(filter.getAuthorId());
        } else if (!filter.isPublished() && filter.getProjectId() != null) {
            return getAllNonPublishedByProjectId(filter.getProjectId());
        }
        return List.of();
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

    @Transactional
    public List<ResourceDto> addPictures(long postId, MultipartFile[] files) {
        Post post = findEntityById(postId);
        postValidator.validateMedia(post, files);
        List<ResourceInfoDto> resourcesInfo = Arrays.stream(files)
                .map(f -> preprocessFiles(postId, f))
                .toList();
        return resourceService.uploadResources(post, resourcesInfo);
    }

    private ResourceInfoDto preprocessFiles(long postId, MultipartFile file) {
        byte[] bytes = imageResizeService.resizeAndConvert(file, maxImageWidth, maxImageHeight);
        return ResourceInfoDto.builder()
                .key(String.format("%s_%s_%s", postId, file.getOriginalFilename(), LocalDateTime.now()))
                .name(file.getOriginalFilename())
                .type("image/jpeg")
                .bytes(bytes)
                .build();
    }

}
