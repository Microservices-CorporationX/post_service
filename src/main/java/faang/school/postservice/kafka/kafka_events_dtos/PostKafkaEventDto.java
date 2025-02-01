package faang.school.postservice.kafka.kafka_events_dtos;

import jakarta.validation.constraints.NotNull;
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
public class PostKafkaEventDto extends AbstractKafkaEventDto {
    @NotNull
    private Long postId;
    private List<Long> authorFollowersIds;
    @NotNull
    private Long authorId;

    @Override
    public String getEventId() {
        return "Event_For_Post: " + postId;
    }
}