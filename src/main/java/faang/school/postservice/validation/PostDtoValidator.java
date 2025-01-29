package faang.school.postservice.validation;

import faang.school.postservice.dto.post.PostDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class PostDtoValidator implements ConstraintValidator<ValidPostDto, PostDto> {

    @Override
    public boolean isValid(PostDto postDto, ConstraintValidatorContext context) {
        boolean isValid = true;

        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            isValid = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Author or Project should be specified for post")
                    .addConstraintViolation();
        }

        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            isValid = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Only one of Author or Project should be the owner of the post")
                    .addConstraintViolation();
        }

        return isValid;
    }
}
