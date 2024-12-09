package faang.school.postservice.client;

import faang.school.postservice.dto.postCorrecter.PostCorrecterRequest;
import faang.school.postservice.dto.postCorrecter.PostCorrecterResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "post-correcter", url = "${side-api.text-gears.base-url}")
public interface PostCorrecterClient {
    @PostMapping("${side-api.text-gears.auto-correct}")
    PostCorrecterResponse checkPost(@Valid @RequestBody PostCorrecterRequest request);
}