package faang.school.postservice.dto.album;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AlbumFilterDto {
    private String titlePattern;
    private LocalDateTime createdAtPattern;
}
