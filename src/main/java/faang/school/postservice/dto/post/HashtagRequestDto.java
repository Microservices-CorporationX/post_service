package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HashtagRequestDto {

    @NotNull
    private Long postId;

    @NotNull
    private String hashtag;
}
