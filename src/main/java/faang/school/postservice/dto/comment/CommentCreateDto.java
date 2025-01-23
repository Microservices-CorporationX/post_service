package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
public record CommentCreateDto(
    @NotBlank
    @Size(max = 4096)
    String content,
    @NotNull
    Long authorId,
    @NotNull
    Long postId
    ) {}
