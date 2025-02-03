package faang.school.postservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostDto {
    private Long id;
    private Long authorId;
    private Long projectId;

    @NotBlank(message = "content must be not blank")
    @NotNull(message = "content must be not null")
    @Size(max = 1000, message = "content must be shorter than 1000 characters")
    private String content;
    List<Long> likesIds;
    List<Long> commentsIds;

    private Boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime scheduledAt;
}
