package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostDto {
    @NotBlank(message = "Post content cannot be blank")
    @Size(max = 4096, message = "Post content must be between 1 and 4096 characters")
    private String content;
    private List<@NotNull(message = "Hashtag don`t have be null") String> hashtags;
    private List<Integer> imageIdToDelete;
}
