package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record PostDto(@NotNull @NotBlank String content,
                      @Positive Long authorId,
                      @Positive Long projectId,
                      Integer likesCount,
                      List<Long> commentIds) {
}
