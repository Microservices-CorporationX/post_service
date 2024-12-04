package faang.school.postservice.validator;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostValidator {

    public void validateAuthorPostCreation(PostDto postDto) {
        if (postDto.authorId() != null && postDto.projectId() != null) {
            log.warn("The author can be a user or a project. Specify something one");
            throw new DataValidationException("The author can be a user or a project. Specify something one");
        }
        if (postDto.authorId() == null && postDto.projectId() == null) {
            log.warn("Specify the author of the post");
            throw new DataValidationException("Specify the author of the post");
        }
    }
}
