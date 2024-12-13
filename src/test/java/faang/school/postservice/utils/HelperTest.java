package faang.school.postservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HelperTest {
    @Mock
    private ObjectMapper objectMapper;

    private Helper helper;

    @BeforeEach
    void setUp() {
        helper = new Helper(objectMapper);
    }

    @Test
    void serializeToJsonShouldReturnJsonStringWhenObjectIsValid() throws JsonProcessingException {
        Object testObject = new Object();
        String expectedJson = "{\"key\":\"value\"}";

        when(objectMapper.writeValueAsString(testObject)).thenReturn(expectedJson);

        String result = helper.serializeToJson(testObject);

        assertEquals(expectedJson, result);
        verify(objectMapper, times(1)).writeValueAsString(testObject);
    }

    @Test
    void serializeToJsonShouldThrowRuntimeExceptionWhenJsonProcessingExceptionOccurs() throws JsonProcessingException {
        Object testObject = new Object();

        when(objectMapper.writeValueAsString(testObject)).thenThrow(new JsonProcessingException("Mocked exception") {});

        RuntimeException exception = assertThrows(RuntimeException.class, () -> helper.serializeToJson(testObject));

        assertEquals("Error serializing object to JSON", exception.getMessage());
        verify(objectMapper, times(1)).writeValueAsString(testObject);
    }
}