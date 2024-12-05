package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.FeignCustomException;
import faang.school.postservice.model.Post;
import feign.FeignException;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostServiceValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void checkPublicationPost(Post post) {
        if (post.isPublished()) {
            throw new DataValidationException("Пост с id " + post.getId() + " уже опубликован");
        }
    }

    public void validateAuthorsEquals(Post oldPost, PostUpdateDto newPost) {
        if (newPost.projectId() == null && newPost.authorId() == null) {
            throw new DataValidationException("Нельзя удалять автора поста");
        }

        if (newPost.authorId() != null && !oldPost.getAuthorId().equals(newPost.authorId()) ||
                newPost.projectId() != null && !oldPost.getProjectId().equals(newPost.projectId())) {
            throw new DataValidationException("Нельзя менять автора поста");
        }
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public void validateAuthorId(long id) {
        log.info("Попытка запроса в UserService");
        try {
            userServiceClient.getUser(id);
        } catch (FeignException exception) {
            log.error("Ошибка при запросе пользователя с id={}", id);
            HttpStatus httpStatus = HttpStatus.valueOf(exception.status());

            if(httpStatus.is4xxClientError()) {
                throw new NoSuchElementException("Пользователь с id={" + id + "} не найден");
            }

            if(httpStatus.is5xxServerError()) {
                throw new RuntimeException();
            }

            throw new FeignCustomException(exception.status(), exception.getMessage());
        }
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public void validateProjectId(long id) {
        log.info("Попытка запроса в ProjectService");

        try {
            projectServiceClient.getProject(id);
        } catch (FeignException exception) {
            log.error("Ошибка при запросе проекта с id={}", id);
            HttpStatus httpStatus = HttpStatus.valueOf(exception.status());

            if(httpStatus.is4xxClientError()) {
                throw new NoSuchElementException("Проект с id={" + id + "} не найден");
            }

            if(httpStatus.is5xxServerError()) {
                throw new RuntimeException();
            }

            throw new FeignCustomException(exception.status(), exception.getMessage());
        }
    }

    @Recover
    public void getProjectRecover(FeignException ex, Long id) {
        log.error("Превышен лимит запросов в ProjectService c id: {}.", id, ex);
        throw new RuntimeException("Превышен лимит запросов в ProjectService: \n" + ex.getMessage());
    }

    @Recover
    public void getUserRecover(FeignException ex, Long id) {
        log.error("Превышен лимит запросов в UserService c id: {}.", id, ex);
        throw new RuntimeException("Превышен лимит запросов в UserService: \n" + ex.getMessage());
    }
}
