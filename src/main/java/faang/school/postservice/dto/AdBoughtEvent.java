package faang.school.postservice.dto;

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
    private BigDecimal paymentAmount;
    private Long adDuration;
    private LocalDateTime receivedAt;
}

