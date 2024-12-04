package faang.school.postservice.service.producer;

public interface MessagePublisher {
    void publish(String channel, Object message);
}
