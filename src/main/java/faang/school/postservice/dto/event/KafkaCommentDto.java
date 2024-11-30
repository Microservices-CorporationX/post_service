package faang.school.postservice.dto.event;

import faang.school.postservice.dto.comment.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaCommentDto {
    private CommentDto commentDto;
    private Long postId;
    private Long authorId;
}
