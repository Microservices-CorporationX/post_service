package faang.school.postservice.publisher;

public interface Publisher<T> {
    void publish(T event);

    Class<T> getEventClass();
}
