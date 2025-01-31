package faang.school.postservice.validator;

import faang.school.postservice.Exception.DataValidationException;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.SavePostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostMapper postMapper;


    public void validateNotPublished(Post post) {
        if (post.getPublishedAt() != null) {
            throw new DataValidationException("Пост уже опубликован и не может быть опубликован повторно");
        }
    }

    public void validateNotDeleted(Post post) {
        if (post.isDeleted()) {
            throw new DataValidationException("Пост уже удалён");
        }
    }

    public void validateDraftPost(SavePostDto savePostDto) {
        Post post = postMapper.toEntity(savePostDto);
        validatePostAuthorExist(post);
    }

    public void validatePostAuthorExist(Post post) {
        if (post.getAuthorId() != null && isUserNotExist(post.getAuthorId())) {
            throw new DataValidationException("Автора не существует");
        }

        if (post.getAuthorId() == null && post.getProjectId() != null && isProjectNotExist(post.getProjectId())) {
            throw new DataValidationException("Проекта не существует");
        }

        if (post.getAuthorId() == null && post.getProjectId() == null) {
            throw new DataValidationException("У поста должен быть либо автор, либо проект");
        }
    }

    private boolean isUserNotExist(Long authorId) {
        return userServiceClient.getUser(authorId) == null;
    }

    private boolean isProjectNotExist(Long projectId) {
        return projectServiceClient.getProject(projectId) == null;
    }
}
