package faang.school.postservice.dto.sightengine.textAnalysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Request {
    @JsonProperty("id")
    private String id;
    @JsonProperty("timestamp")
    private double timestamp;
    @JsonProperty("operations")
    private int operations;
}
