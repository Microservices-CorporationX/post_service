package faang.school.postservice.exception;

import org.springframework.http.HttpStatus;

/**
 * Исключение, выбрасываемое в случае, когда сущность не найдена в системе.
 * Это исключение наследует {@link RuntimeException} и используется для
 * сигнализации о том, что запрашиваемая сущность отсутствует.
 * <p>
 * Обычно это исключение применяется, когда операция требует существования
 * сущности (например, поиск по ID), но сущность не была найдена.
 */
public class EntityNotFoundException extends ErrorResponse {
    public EntityNotFoundException(String message) {
        super(message, "Entity not found", HttpStatus.NOT_FOUND);
    }
}
