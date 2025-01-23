package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CommentUpdateDto (
    @NotNull
    Long id,
    @NotBlank
    @Size(max = 4096)
    String content,
    @NotNull
    Long editorId
) {}
