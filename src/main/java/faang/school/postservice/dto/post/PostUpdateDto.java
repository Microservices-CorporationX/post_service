package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.validation.annotation.Validated;

@Schema(description = "Post Update Dto")
@Validated
public record PostUpdateDto(
        @Schema(description = "id пользователя")
        @NotNull Long id,
        @Schema(description = "поле для создания контента")
        @NotBlank String content,
        @Schema(description = "id автора")
        @Nullable Long authorId,
        @Schema(description = "id проэкта")
        @Nullable Long projectId,
        @Schema(description = "ID для лайков")
        @Nullable List<Long> likesIds,
        @Schema(description = "ID для комментариев")
        @Nullable List<Long> commentsIds,
        @Schema(description = "состаояние поста, если опубликовано true")
        @NotNull boolean published,
        @Schema(description = "состояние поста,если удалено true")
        @NotNull boolean deleted,
        @Schema(description = "время когда пост был опубликован")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Nullable LocalDateTime publishedAt,
        @Schema(description = "время когда пост был создан")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Nullable LocalDateTime createdAt
) {
}
