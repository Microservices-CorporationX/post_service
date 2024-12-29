package faang.school.postservice.dto.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectDto {
    @NotNull(message = "ID should not be null")
    @Positive(message = "ID must be positive")
    private long id;

    @NotBlank(message = "Title should not be blank")
    @Size(max = 255, message = "Title should not exceed 255 characters")
    private String title;
}
