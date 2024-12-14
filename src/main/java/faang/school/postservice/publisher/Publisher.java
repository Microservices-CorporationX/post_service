package faang.school.postservice.publisher;

public interface Publisher<T> {
    void publish(Object event);

    Class<?> getEventClass();
}
