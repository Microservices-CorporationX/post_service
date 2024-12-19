package faang.school.postservice.dto.album;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumUpdateDto {

    @NotNull
    @Positive(message = "Id is required")
    private long id;

    @Size(min = 3, max = 128, message = "Title must be between 3 and 128 characters.")
    private String title;

    @Size(min = 10, max = 4096, message = "Description must be between 10 and 4096 characters.")
    private String description;

    private List<@Positive Long> postsId;
}
