package faang.school.postservice.dto.hashtag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record HashtagCreateDto(
        @NotBlank
        @Size(max = 64)
        String name
) {
}
