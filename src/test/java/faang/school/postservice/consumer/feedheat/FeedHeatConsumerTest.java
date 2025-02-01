package faang.school.postservice.consumer.feedheat;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.feedheat.FeedHeatEvent;
import faang.school.postservice.service.feed.cache.HeaterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FeedHeatConsumerTest {
    @Spy
    private ObjectMapper objectMapper;
    @Mock
    private HeaterService heaterService;
    @InjectMocks
    private FeedHeatConsumer feedHeatConsumer;
    private Acknowledgment ack = Mockito.mock(Acknowledgment.class);

    @Test
    void testConsume() throws Exception {
        FeedHeatEvent event = new FeedHeatEvent(List.of(1L, 2L, 3L, 4L));
        feedHeatConsumer.consume(objectMapper.writeValueAsString(event), ack);
        event.getUserIds().forEach(id -> verify(heaterService).heatUser(id));
        verify(ack).acknowledge();
    }
}