package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;

    @NotBlank(message = "Comment content is required")
    @Length(max = 4096, message = "Comment content can not be longer than 4096 characters")
    private String content;

    @NotNull(message = "Comment author id is required")
    private Long authorId;

    @NotNull(message = "Comment post id is required")
    private Long postId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
