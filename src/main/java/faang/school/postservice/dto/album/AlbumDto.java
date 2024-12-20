package faang.school.postservice.dto.album;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class AlbumDto {

    @Min(value = 1, message = "Некорректный ID альбома")
    @Schema(description = "id запроса")
    private long id;

    @NotBlank(message = "Имя не должно быть пустым.")
    @Schema(description = "заголовок")
    private String title;

    @NotBlank(message = "Описание не должно быть пустым.")
    @Schema(description = "описание")
    private String description;

    @Min(value = 1, message = "Некорректный ID автора")
    @Schema(description = "id автора")
    private long authorId;

}
