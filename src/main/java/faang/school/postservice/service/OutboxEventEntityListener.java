package faang.school.postservice.service;

import faang.school.postservice.model.OutboxEvent;
import jakarta.persistence.PostPersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventEntityListener {
    private static OutboxEventProcessor outboxProcessor;

    @Autowired
    public void setOutboxProcessor(OutboxEventProcessor outboxProcessor) {
        OutboxEventEntityListener.outboxProcessor = outboxProcessor;
    }

    @PostPersist
    public void onPostPersist(OutboxEvent event) {
        outboxProcessor.triggerProcessing();
    }
}
