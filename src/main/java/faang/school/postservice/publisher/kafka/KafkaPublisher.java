package faang.school.postservice.publisher.kafka;

public interface KafkaPublisher<T> {
    void publish(T event);
}
