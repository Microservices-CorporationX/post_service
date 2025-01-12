package faang.school.postservice.dto.event;

import faang.school.postservice.dto.comment.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KafkaCommentDto {
    private CommentDto commentDto;
    private Long postId;
    private Long authorId;
}
