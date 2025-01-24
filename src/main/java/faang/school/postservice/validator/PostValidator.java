package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.posts.PostSaveDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataNotFoundException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.IntegrationException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostValidator {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;


    private static final String USER_NOT_FOUND_ERR_MSG = "Пользователь с id:%s не найден!";
    private static final String PROJECT_NOT_FOUND_ERR_MSG = "Пользователь с id:%s не найден!";

    public void validatePost(PostSaveDto postSaveDto) {
        if (postSaveDto.getAuthorId() == null && postSaveDto.getProjectId() == null) {
            throw new DataValidationException("Должно быть заполнено одно из значений: authorId или projectId");
        } else {
            if (postSaveDto.getAuthorId() != null) {
                userExist(postSaveDto.getAuthorId());
            } else {
                projectExist(postSaveDto.getProjectId());
            }
        }
        if (postSaveDto.getContent() == null || postSaveDto.getContent().isBlank()) {
            throw new DataValidationException("Контент не может быть пустым");
        } else if (postSaveDto.getContent().length() > 4096) {
            throw new DataValidationException("Длина контента не может превышать 4096 символов");
        }
    }

    public void userExist(long authorId) {
        try {
            UserDto userResponse = userServiceClient.getUser(authorId);
            log.debug("userResponse response: {}", userResponse);
            if (userResponse == null || userResponse.id() == null) {
                throw new DataNotFoundException(String.format(USER_NOT_FOUND_ERR_MSG, authorId));
            }
        } catch (FeignException e) {
            log.error("userResponse response: {}", e.toString());
            if (e.status() == HttpStatus.NOT_FOUND.value()) {
                throw new DataNotFoundException(String.format(USER_NOT_FOUND_ERR_MSG, authorId));
            } else {
                throw new IntegrationException(e.getMessage());
            }
        }
    }

    public void projectExist(long projectId) {
        try {
            ProjectDto projectResponse = projectServiceClient.getProject(projectId);
            log.debug("projectResponse response: {}", projectResponse);
            if (projectResponse == null) {
                throw new DataNotFoundException(String.format(PROJECT_NOT_FOUND_ERR_MSG, projectId));
            }
        } catch (FeignException e) {
            log.error("projectResponse response: {}", e.toString());
            if (e.status() == HttpStatus.NOT_FOUND.value()) {
                throw new DataNotFoundException(String.format(PROJECT_NOT_FOUND_ERR_MSG, projectId));
            } else {
                throw new IntegrationException(e.getMessage());
            }
        }
    }
}
