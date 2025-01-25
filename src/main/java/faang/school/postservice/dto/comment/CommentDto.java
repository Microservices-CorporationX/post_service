package faang.school.postservice.dto.comment;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;

    @Positive
    private Long authorId;

    @Positive
    private Long postId;

    @NotBlank
    @Size(max = 4096)
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}