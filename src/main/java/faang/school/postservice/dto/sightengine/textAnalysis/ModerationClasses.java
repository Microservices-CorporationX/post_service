package faang.school.postservice.dto.sightengine.textAnalysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class ModerationClasses {
    @JsonProperty("available")
    private List<String> available;
    @JsonProperty("sexual")
    private double sexual;
    @JsonProperty("discriminatory")
    private double discriminatory;
    @JsonProperty("insulting")
    private double insulting;
    @JsonProperty("violent")
    private double violent;
    @JsonProperty("toxic")
    private double toxic;
}
