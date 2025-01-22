package faang.school.postservice.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.posts.PostSaveDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostValidator {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final UserContext userContext;


    private static final String INTEGRATION_ERR_MSG = "Ошибка взаимодействия с сервисом: %s";

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
        userContext.setUserId(authorId);
        try {
            UserDto userResponse = userServiceClient.getUser(authorId);
            log.debug("userResponse response: {}", new ObjectMapper().writeValueAsString(userResponse));
            if (userResponse == null || userResponse.id() == null) {
                throw new DataValidationException(String.format("Пользователь с id:%s не найден!", authorId));
            }
        } catch (FeignException e) {
            log.error("userResponse response: {}", e.toString());
            throw new IllegalArgumentException(String.format(INTEGRATION_ERR_MSG, "user-service"));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new IllegalArgumentException(String.format(INTEGRATION_ERR_MSG, "user-service"));
        }
        userContext.clear();
    }

    public void projectExist(long projectId) {
        userContext.setUserId(projectId);
        try {
            ProjectDto projectResponse = projectServiceClient.getProject(projectId);
            log.debug("projectResponse response: {}", new ObjectMapper().writeValueAsString(projectResponse));
            if (projectResponse == null) {
                throw new DataValidationException(String.format("Проект с id:%s не найден!", projectId));
            }
        } catch (FeignException e) {
            log.error("projectResponse response: {}", e.toString());
            throw new IllegalArgumentException(String.format(INTEGRATION_ERR_MSG, "project-service"));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new IllegalArgumentException(String.format(INTEGRATION_ERR_MSG, "project-service"));
        }
        userContext.clear();
    }
}
