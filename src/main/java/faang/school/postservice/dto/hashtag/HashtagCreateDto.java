package faang.school.postservice.dto.hashtag;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public record HashtagCreateDto(
        @NotBlank
        String name,
        List<Long> postIds
) {
}
