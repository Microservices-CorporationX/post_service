package ru.corporationx.postservice.dto.grammar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpellResultDto {
    @JsonProperty("pos")
    private int position;
    @JsonProperty("len")
    private int oldLength;
    @JsonProperty("word")
    private String originalWord;
    @JsonProperty("s")
    private String[] suggestions;
}