package faang.school.postservice.kafka.kafka_events_dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentKafkaEventDto extends AbstractKafkaEventDto{
    private Long commentId;
    private Long commentAuthorId;
    private Long postAuthorId;
    private Long postId;
    private String commentContent;

    @Override
    public String getEventId() {
        return "Event_For_Comment: " + commentId;
    }
}