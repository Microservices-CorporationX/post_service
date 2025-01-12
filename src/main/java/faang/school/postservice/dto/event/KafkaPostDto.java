package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaPostDto {
    private Long id;
    private Long authorId;
    private List<Long> subscriberIds;
}
