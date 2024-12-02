package faang.school.postservice.dto.sightengine.textAnalysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class ModerationClasses {
    @JsonProperty("available")
    private List<String> available;

    @JsonProperty("sexual")
    private Double sexual;

    @JsonProperty("discriminatory")
    private Double discriminatory;

    @JsonProperty("insulting")
    private Double insulting;

    @JsonProperty("violent")
    private Double violent;

    @JsonProperty("toxic")
    private Double toxic;
}
