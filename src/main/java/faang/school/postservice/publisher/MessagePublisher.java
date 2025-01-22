package faang.school.postservice.publisher;

import faang.school.postservice.dto.Event;

public interface MessagePublisher {
    void publishMessage(Event event);
}
