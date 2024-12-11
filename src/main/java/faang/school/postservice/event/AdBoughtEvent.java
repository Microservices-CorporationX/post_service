package faang.school.postservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AdBoughtEvent {
    private Long postId;
    private Long actorId;
    private Long receiverId;
    private BigDecimal paymentAmount;
    private Long adDuration;
    private LocalDateTime receivedAt;
}

