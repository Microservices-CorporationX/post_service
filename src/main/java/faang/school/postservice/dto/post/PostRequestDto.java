package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
    @NotNull
    private Long authorId;
    @NotNull
    private Long projectId;
    @NotBlank
    private String content;
    private List<MultipartFile> images;
    private List<MultipartFile> audio;
}