package faang.school.postservice.dto.like;

import lombok.Builder;

@Builder
public record LikePostDto(Long userId,
                          Long postId) {
}
