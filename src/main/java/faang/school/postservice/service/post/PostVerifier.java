package faang.school.postservice.service.post;


import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static faang.school.postservice.exception.message.PostOperationExceptionMessage.DELETED_STATUS_UPDATE_EXCEPTION;
import static faang.school.postservice.exception.message.PostOperationExceptionMessage.LIKES_UPDATE_EXCEPTION;
import static faang.school.postservice.exception.message.PostOperationExceptionMessage.PUBLISHED_DATE_UPDATE_EXCEPTION;
import static faang.school.postservice.exception.message.PostValidationExceptionMessage.NON_EXISTING_PROJECT_EXCEPTION;
import static faang.school.postservice.exception.message.PostValidationExceptionMessage.NON_EXISTING_USER_EXCEPTION;
import static faang.school.postservice.exception.message.PostValidationExceptionMessage.NON_MATCHING_AUTHORS_EXCEPTION;

@Component
@RequiredArgsConstructor
class PostVerifier {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    public void verifyAuthorExistence(Long authorId, Long projectId) {
        if (authorId != null) {
            verifyUserExistence(authorId);
        }

        if (projectId != null) {
            verifyProjectExistence(projectId);
        }
    }

    public void verifyUserExistence(long userId) {
        if (!userServiceClient.existsById(userId)) {
            throw new DataValidationException(NON_EXISTING_USER_EXCEPTION.getMessage());
        }
    }

    public void verifyProjectExistence(long projectId) {
        if (!projectServiceClient.existsById(projectId)) {
            throw new DataValidationException(NON_EXISTING_PROJECT_EXCEPTION.getMessage());
        }
    }

    public void verifyPostMatchingWithSystem(PostDto postDto, Post postToBeUpdated) {
        Long userAuthorIdFromDto = postDto.getAuthorId();
        if (userAuthorIdFromDto != null && !Objects.equals(postToBeUpdated.getAuthorId(), userAuthorIdFromDto)) {
            throw new DataValidationException(NON_MATCHING_AUTHORS_EXCEPTION.getMessage());
        }

        Long projectAuthorIdFromDto = postDto.getProjectId();
        if (projectAuthorIdFromDto != null && !Objects.equals(postToBeUpdated.getProjectId(), projectAuthorIdFromDto)) {
            throw new DataValidationException(NON_MATCHING_AUTHORS_EXCEPTION.getMessage());
        }

        Set<Long> likesOfPostToBeUpdated = postToBeUpdated.getLikes().stream()
                .map(Like::getId)
                .collect(Collectors.toSet());
        if (!likesOfPostToBeUpdated.containsAll(postDto.getLikesIds())) {
            throw new DataValidationException(LIKES_UPDATE_EXCEPTION.getMessage());
        }

        Set<Long> commentsOfPostToBeUpdated = postToBeUpdated.getComments().stream()
                .map(Comment::getId)
                .collect(Collectors.toSet());
        if (!commentsOfPostToBeUpdated.containsAll(postDto.getCommentsIds())) {
            throw new DataValidationException(LIKES_UPDATE_EXCEPTION.getMessage());
        }

        LocalDateTime publishedAtFromDto = postDto.getPublishedAt();
        if (publishedAtFromDto != null && postToBeUpdated.isPublished() && !postToBeUpdated.getPublishedAt().equals(publishedAtFromDto)) {
            throw new DataValidationException(PUBLISHED_DATE_UPDATE_EXCEPTION.getMessage());
        }

        if (!postDto.getDeleted().equals(postToBeUpdated.isDeleted())) {
            throw new DataValidationException(DELETED_STATUS_UPDATE_EXCEPTION.getMessage());
        }
    }
}
