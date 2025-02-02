package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePostDto {
    @NotNull(message = "Автор или проект должны быть указаны")
    private Long authorId;

    private Long projectId;

    @Size(min = 1, max = 4096, message = "Контент не может быть пустым и не должен превышать 4096 символов")
    private String content;

    private Boolean published;
}