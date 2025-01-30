package faang.school.postservice.dto.kafka_events;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostKafkaEventDto extends AbstractKafkaEventDto {
    @NotNull
    private Long postId;
    private List<Long> authorFollowersIds;
    @NotNull
    private Long authorId;
}