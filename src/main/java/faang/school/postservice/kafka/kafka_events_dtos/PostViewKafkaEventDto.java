package faang.school.postservice.kafka.kafka_events_dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostViewKafkaEventDto extends AbstractKafkaEventDto {
    private Long postId;

    @Override
    public String getEventId() {
        return "View_Event_For_Post: " + postId;
    }
}