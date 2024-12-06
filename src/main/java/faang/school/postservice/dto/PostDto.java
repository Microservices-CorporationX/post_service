package faang.school.postservice.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class PostDto {
    @Positive(message = "ID is required")
    private long id;
}
