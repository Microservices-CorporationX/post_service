package faang.school.postservice.dto.postCorrecter.textGears;

import faang.school.postservice.dto.postCorrecter.PostCorrecterRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TextGearsRequest implements PostCorrecterRequest {
    @NotBlank(message = "The post should contain some text")
    private String text;
    private String key;
}