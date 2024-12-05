package faang.school.postservice.dto.post;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record PostCreateDto(
        @NotBlank(message = "Поле \"content\" не заполнено") String content,
        @Nullable Long authorId,
        @Nullable Long projectId
) {

}
