package faang.school.postservice.dto.comment;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentDto {
    private Long id;

    @NotNull
    @Positive
    private Long authorId;

    @NotNull
    @Positive
    private Long postId;

    @NotBlank
    @Size(max = 4096)
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
/* пришло автор - его id, id post, content, timedata */