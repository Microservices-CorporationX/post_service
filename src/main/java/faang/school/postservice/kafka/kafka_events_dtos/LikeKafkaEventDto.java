package faang.school.postservice.kafka.kafka_events_dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeKafkaEventDto extends AbstractKafkaEventDto{
    private Long authorId;
    private Long postId;
    private Long likeId;

    @Override
    public String getEventId() {
        return "Event_For_Like: " + likeId;
    }
}