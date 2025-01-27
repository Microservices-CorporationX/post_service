package faang.school.postservice.dto.post;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HashtagRequestDto {

    @NotNull
    @Min(1)
    private Long postId;

    @NotNull
    @NotBlank
    private String hashtag;
}
