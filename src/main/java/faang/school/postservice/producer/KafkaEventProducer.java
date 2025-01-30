package faang.school.postservice.producer;

public interface KafkaEventProducer<T> {
    void send(T event);
}
