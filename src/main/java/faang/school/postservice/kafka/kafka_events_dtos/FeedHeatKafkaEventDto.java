package faang.school.postservice.kafka.kafka_events_dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedHeatKafkaEventDto extends AbstractKafkaEventDto {
    private List<Long> usersIds;

    @Override
    public String getEventId() {
        return "Feed_Heat_Event";
    }
}