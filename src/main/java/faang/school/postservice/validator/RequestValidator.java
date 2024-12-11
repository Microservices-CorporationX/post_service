package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestValidator {
    private final HttpServletRequest request;

    public void validateAuthorUpdatesPost(Post post) {
        String userHeader = request.getHeader("x-user-id");
        String projectHeader = request.getHeader("x-project-id");

        if (userHeader != null) {
            Long headerUserId = Long.valueOf(userHeader);
            if (!headerUserId.equals(post.getAuthorId())) {
                log.warn("User ID in header does not match the author ID of the post");
                throw new DataValidationException("User ID does not match the author ID of the post");
            }
        } else if (projectHeader != null) {
            Long headerProjectId = Long.valueOf(projectHeader);
            if (!headerProjectId.equals(post.getProjectId())) {
                log.warn("Project ID in header does not match the project ID of the post");
                throw new DataValidationException("Project ID does not match the project ID of the post");
            }
        } else {
            log.warn("Neither x-user-id nor x-project-id header is present");
            throw new DataValidationException("Neither x-user-id nor x-project-id header is present");
        }
    }
}
