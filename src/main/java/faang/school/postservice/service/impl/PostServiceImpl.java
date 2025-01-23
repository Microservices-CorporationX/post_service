package faang.school.postservice.service.impl;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.PostValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostMapper postMapper;

    @Override
    public PostDto createDraft(PostDto postDto) {
        postDto.setCreatedAt(LocalDateTime.now());
        postDto.setPublished(false);
        Post post = postMapper.toEntity(postDto);

        if ((Objects.nonNull(postDto.getAuthorId()) && Objects.isNull(postDto.getProjectId()))) {
            try {
                userServiceClient.getUser(postDto.getAuthorId());

                return postMapper.toDto(postRepository.save(post));
            } catch (FeignException exception) {
                if (exception.status() == 404) {
                    throw new PostValidationException(String.format("User with ID %d not found", postDto.getAuthorId()));
                } else {
                    throw new PostValidationException("Error occurred while fetching user information: " + exception.getMessage());
                }
            }
        } else if (Objects.isNull(postDto.getAuthorId()) && Objects.nonNull(postDto.getProjectId())) {
            try {
                projectServiceClient.getProject(postDto.getProjectId());

                return postMapper.toDto(postRepository.save(post));
            } catch (FeignException exception) {
                if (exception.status() == 404) {
                    throw new PostValidationException(String.format("Project with ID %d not found", postDto.getProjectId()));
                } else {
                    throw new PostValidationException("Error occurred while fetching project information: " + exception.getMessage());
                }
            }
        }
        throw new PostValidationException("Post must have exactly one author");
    }

    @Override
    public PostDto publish(Long postId) {
        return postRepository.findById(postId)
                .map(post -> {
                            if (post.isPublished()) {
                                throw new PostValidationException
                                        (String.format("Post with ID %s is already published", postId));
                            }
                            post.setPublishedAt(LocalDateTime.now());
                            post.setPublished(true);
                            return postMapper.toDto(postRepository.save(post));
                        }
                )
                .orElseThrow(() -> new PostNotFoundException(String.format("Post with id %s not found", postId)));
    }

    @Override
    public PostDto update(PostDto postDto) {
        return null;
    }
}
