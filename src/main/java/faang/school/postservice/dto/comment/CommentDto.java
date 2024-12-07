package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;

    @NotBlank(message = "Content should not be blank")
    @Size(max = 4096, message = "Content must not exceed 4096 characters")
    private String content;

    @Min(1)
    @NotNull(message = "authorId should not be null")
    private Long authorId;

    private List<Long> likeIds;

    @Min(1)
    @NotNull(message = "postId should not be null")
    private Long postId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
