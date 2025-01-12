package faang.school.postservice.dto.event;

import java.util.List;

public record KafkaFeedHeaterDto(List<Long> userIds) {
}
