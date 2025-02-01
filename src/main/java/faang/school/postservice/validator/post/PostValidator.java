package faang.school.postservice.validator.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostAuthorFilterDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostValidator {
    @Value("${app.posts.files.max-file-size}")
    private long maxFileSize;
    @Value("${app.posts.files.max-post-files-count}")
    private int maxPostFiles;

    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;

    public void validateCreation(@Valid PostDto postDto) {
        if (postDto.getProjectId() != null && postDto.getAuthorId() != null) {
            throw new DataValidationException("Post must be have only user or project");
        }

        if (postDto.getProjectId() != null) {
            validateProject(postDto.getProjectId());
        } else if (postDto.getAuthorId() != null) {
            validateUser(postDto.getAuthorId());
        }
    }

    public void validateUpdate(Post post, @Valid PostDto postDto) {
        if (!Objects.equals(post.getAuthorId(), postDto.getAuthorId())) {
            throw new DataValidationException("Author cannot be changed");
        }
        if (!Objects.equals(post.getProjectId(), postDto.getProjectId())) {
            throw new DataValidationException("Project cannot be changed");
        }
    }

    public void validateProject(long projectId) {
        try {
            projectServiceClient.getProject(projectId);
        } catch (FeignException e) {
            log.error("Error when getting project from ProjectService", e);
            throw new DataValidationException("Project id is not exist");
        }
    }

    public void validateUser(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new DataValidationException("User id is not exist");
        }
    }

    public void validateFilter(PostAuthorFilterDto filter) {
        if (filter.getProjectId() == null && filter.getAuthorId() == null) {
            throw new DataValidationException("AuthorId or projectId are required");
        }
        if (filter.getProjectId() != null && filter.getAuthorId() != null) {
            throw new DataValidationException("Only authorId or only projectId are required");
        }
    }

    public void validateMedia(Post post, MultipartFile[] files) {
        if (post.getResources().size() + files.length >= maxPostFiles) {
            throw new DataValidationException(String.format("Result count media is more then %s", maxPostFiles));
        }
        for (var file : files) {
            if (file.getSize() > maxFileSize) {
                throw new DataValidationException("File size is too big");
            }
            validateImageContentType(file.getContentType());
        }
    }

    private void validateImageContentType(String contentType) {
        if (contentType == null || !contentType.contains("image")) {
            throw new DataValidationException("Unsupported media type for post content");
        }
    }

}
