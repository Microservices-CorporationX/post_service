package faang.school.postservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "DTO representing a post, including content, author, likes, comments, and timestamps")
@Data
public class PostDto {
    @Schema(description = "Unique identifier of the post", example = "1")
    private Long id;

    @NotBlank(message = "content must be not blank")
    @NotNull(message = "content must be not null")
    @Size(max = 1000, message = "content must be shorter than 1000 characters")
    @Schema(description = "Content of the post. It must not be blank or null, and should be less than 1000 characters.",
            example = "This is a post content.")
    private String content;

    @Schema(description = "ID of the author of the post", example = "123")
    private Long authorId;
    @Schema(description = "ID of the associated project for the post", example = "456")
    private Long projectId;
    @Schema(description = "List of IDs of likes associated with the post", example = "[1, 2, 3]")
    private List<Long> likeIds;
    @Schema(description = "List of IDs of comments associated with the post", example = "[101, 102, 103]")
    private List<Long> commentIds;
    @Schema(description = "Indicates whether the post is deleted", example = "false")
    private Boolean deleted;
    @Schema(description = "Timestamp when the post was created", example = "2024-12-13T10:15:30")
    private LocalDateTime createdAt;
    @Schema(description = "Timestamp when the post was last updated", example = "2024-12-13T10:20:30")
    private LocalDateTime updatedAt;
    @Schema(description = "Timestamp when the post was published", example = "2024-12-13T11:00:00")
    private LocalDateTime publishedAt;
    @Schema(description = "Timestamp when the post is scheduled to be published", example = "2024-12-14T12:00:00")
    private LocalDateTime scheduledAt;
}
