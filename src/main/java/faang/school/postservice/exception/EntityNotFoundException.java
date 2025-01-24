package faang.school.postservice.exception;

/**
 * Исключение, которое выбрасывается, когда сущность не найдена в базе данных.
 * <p>
 * Это исключение может быть использовано для обработки ситуаций, когда
 * попытка найти сущность по идентификатору или другому критерию не увенчалась успехом.
 * Обычно используется для возврата ошибки в случае отсутствия данных.
 * </p>
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}