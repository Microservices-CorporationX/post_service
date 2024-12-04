package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record LikePostDto(
       @Positive Long id,
       @Positive Long userId,
       @Positive Long postId
){
}