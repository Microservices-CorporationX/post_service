package faang.school.postservice.kafka.kafka_events_dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedHeaterKafkaEventDto extends AbstractKafkaEventDto{
    private List<Long> userIds;

    @Override
    public String getEventId() {
        return "Feed_Heat_Event";
    }
}