package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCreatedEvent implements Serializable {
    private Long postId;
    private Long authorId;
    private List<Long> subscriberIds;
    private Instant createdAt;
}