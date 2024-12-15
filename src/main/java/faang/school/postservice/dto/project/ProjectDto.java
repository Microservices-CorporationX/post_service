package faang.school.postservice.dto.project;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectDto {
    @Min(value = 1, message = "ID must be a positive number")
    private long id;
    @NotBlank(message = "Title should not be blank")
    @Size(max = 255, message = "Title should not exceed 255 characters")
    private String title;
}
