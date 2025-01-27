package faang.school.postservice.dto.post;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostUpdateDto {
    @Size(max = 4096)
    private String content;
    private List<Long> hashtagIds;
}