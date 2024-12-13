package faang.school.postservice.dto.analytics;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsEventDto {
    @NotNull
    private Long receiverId;

    @NotNull
    private Long actorId;

    @JsonProperty(defaultValue = "-1")
    private int eventType;

    private LocalDateTime receivedAt;
}
