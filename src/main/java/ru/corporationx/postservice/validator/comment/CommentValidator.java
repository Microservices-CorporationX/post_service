package ru.corporationx.postservice.validator.comment;

import ru.corporationx.postservice.client.UserServiceClient;
import ru.corporationx.postservice.dto.comment.CommentDto;
import ru.corporationx.postservice.exception.DataValidationException;
import ru.corporationx.postservice.model.Comment;
import feign.FeignException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@AllArgsConstructor
public class CommentValidator {
    private final UserServiceClient userServiceClient;

    public void validateCreation(@Valid CommentDto comment) {
        if (comment.getAuthorId() == null) {
            throw new DataValidationException("Comment author is empty");
        }

        try {
            userServiceClient.getUser(comment.getAuthorId());
        } catch (FeignException e) {
            throw new DataValidationException("Author id is not exist");
        }
    }

    public void validateUpdate(Comment comment, @Valid CommentDto dto) {
        if (!Objects.equals(comment.getAuthorId(), dto.getAuthorId())) {
            throw new DataValidationException("Comment author cannot be changed");
        }
        if (!Objects.equals(comment.getPost().getId(), dto.getPostId())) {
            throw new DataValidationException("Comment post cannot be changed");
        }
    }

}
