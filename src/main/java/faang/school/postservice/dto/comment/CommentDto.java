package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentDto {

    private Long id;

    @NotBlank(message = "Content cannot be blank.")
    @Size(max = 4096, message = "Content cannot exceed 4096 characters.")
    private String content;

    @NotNull(message = "Author ID cannot be null.")
    private Long authorId;

    @NotNull(message = "Post ID cannot be null.")
    private Long postId;
}
