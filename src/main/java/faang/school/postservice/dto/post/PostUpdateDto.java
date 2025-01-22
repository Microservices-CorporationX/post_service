package faang.school.postservice.dto.post;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateDto {
    @Size(max = 4096)
    private String content;
}
