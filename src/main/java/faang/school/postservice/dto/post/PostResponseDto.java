package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto implements Serializable {

    private Long id;

    private String content;

//    private Long authorId;
//
//    private Long projectId;

//    private LocalDateTime createdAt;
//
//    private LocalDateTime updatedAt;
}
