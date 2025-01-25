package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UpdateCommentDto {

    @Positive
    Long id;

    @Positive
    Long authorId;

    @NotBlank
    @Size(max = 4096)
    private String content;
}
