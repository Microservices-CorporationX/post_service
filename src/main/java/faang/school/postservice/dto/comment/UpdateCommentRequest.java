package faang.school.postservice.dto.comment;

import lombok.Data;

@Data
public class UpdateCommentRequest {
    private Long id;
    private Long authorId;
    private String content;
}
