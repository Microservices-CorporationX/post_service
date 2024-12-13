package faang.school.postservice.validator.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import feign.FeignException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@AllArgsConstructor
public class PostValidator {
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

}
