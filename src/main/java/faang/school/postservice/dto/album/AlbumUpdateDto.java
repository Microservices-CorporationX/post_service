package faang.school.postservice.dto.album;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AlbumUpdateDto {
    @Size(max = 256)
    private String title;
    @Size(max = 4096)
    private String description;
}
