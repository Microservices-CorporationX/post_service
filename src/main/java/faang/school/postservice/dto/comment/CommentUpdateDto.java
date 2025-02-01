package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentUpdateDto {

    @NotEmpty
    @Size(min = 1, max = 4096, message = "Комментарий может содержать от 1 до 4096 символов")
    private String content;
}
