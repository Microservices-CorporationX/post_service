package faang.school.postservice.dto.album;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Month;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumFilterDto {

    @Size(min = 1, max = 128, message = "Title pattern must be between 1 and 128 characters.")
    private String titlePattern;

    @Size(min = 1, max = 4096, message = "Description pattern must be between 1 and 4096 characters.")
    private String descriptionPattern;

    private Month month;
}
