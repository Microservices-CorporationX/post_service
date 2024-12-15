package faang.school.postservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO representing a comment, including content, author, likes, and timestamps")
public class CommentDto {
    @Schema(description = "Unique identifier for the comment", example = "1")
    private Long id;
    @NotBlank
    @Size(max = 4096, message = "The size exceeds 4096 characters")
    @Schema(description = "The content of the comment. Maximum length is 4096 characters.", example = "This is a comment.")
    private String content;
    @NotNull
    @Schema(description = "ID of the author of the comment", example = "123")
    private Long authorId;
    @NotNull
    @Schema(description = "List of IDs of likes associated with the comment", example = "[1, 2, 3]")
    private List<Long> likeIds;
    @Schema(description = "ID of the post to which the comment belongs", example = "456")
    private Long postId;
    @Schema(description = "Timestamp when the comment was created", example = "2024-12-13T10:15:30")
    private LocalDateTime createdAt;
    @Schema(description = "Timestamp when the comment was last updated", example = "2024-12-13T10:20:30")
    private LocalDateTime updatedAt;
}
