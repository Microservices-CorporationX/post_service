package faang.school.postservice.validator.like;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    private final ProjectServiceClient projectServiceClient;

    public void validateProject(Long projectId) {
        if (projectId == null) {
            throw new DataValidationException("Id проекта не может быть равно null");
        }
        projectServiceClient.getProject(projectId);
    }
}
