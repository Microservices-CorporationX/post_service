package ru.corporationx.postservice.publisher;

import org.springframework.stereotype.Component;

@Component
public interface MessagePublisher {

    void publish(Object object);
}
