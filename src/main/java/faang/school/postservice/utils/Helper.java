package faang.school.postservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Helper {
    private final ObjectMapper objectMapper;

    public String serializeToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new JsonProcessingException("Error serializing object to JSON");
        }
    }
}
