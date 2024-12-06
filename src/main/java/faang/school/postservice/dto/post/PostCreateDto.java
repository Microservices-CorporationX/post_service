package faang.school.postservice.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "PostCreate Dto")
public record PostCreateDto(
        @Schema(description = "Контент поста")
        @NotBlank(message = "Поле \"content\" не заполнено") String content,
        @Schema(description = "id автора")
        @Nullable Long authorId,
        @Schema(description = "id проэкта")
        @Nullable Long projectId
) {

}
