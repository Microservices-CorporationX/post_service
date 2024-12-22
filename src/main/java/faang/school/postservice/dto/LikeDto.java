package faang.school.postservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "DTO for Like entity")
@Data
public class LikeDto {
    @NotNull
    @Schema(description = "Unique identifier of the like", example = "1")
    @NotNull(message = "Id cannot be null")
    private Long id;
    @NotNull
    @Schema(description = "ID of the user who liked the post or comment", example = "123")
    @NotNull(message = "User id cannot be null")
    private Long userId;
    @Schema(description = "ID of the comment that was liked", example = "456")
    private Long commentId;
    @Schema(description = "ID of the post that was liked", example = "789")
    private Long postId;
    @Schema(description = "The timestamp when the like was created", example = "2024-12-13T10:15:30")
    private LocalDateTime createdAt;
}
