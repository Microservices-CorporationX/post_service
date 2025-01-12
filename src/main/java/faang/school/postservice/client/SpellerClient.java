package faang.school.postservice.client;

import faang.school.postservice.config.web.FormFeignEncoderConfig;
import faang.school.postservice.dto.grammar.SpellResultDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "speller-client",
        url = "${post-correction.api-url}",
        configuration = FormFeignEncoderConfig.class)
public interface SpellerClient {
    @PostMapping(value = "/checkText", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    List<SpellResultDto> checkText(@RequestBody Map<String, ?> data);
}
