package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UpdateCommentDto {
    //private long id;
    @NotBlank
    @Size(max = 4096)
    private String content;
    private long authorId;
    private LocalDateTime updatedAt;
}
