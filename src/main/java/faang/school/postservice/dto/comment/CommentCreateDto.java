package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentCreateDto {
    @NotBlank
    @Size(max = 4096)
    private String content;
    @NotNull
    @Positive
    private Long authorId;
    @NotNull
    @Positive
    private Long postId;
    private LocalDateTime createdAt;
}
