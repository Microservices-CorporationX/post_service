package faang.school.postservice.exception;

/**
 * Это исключение выбрасывается при нарушении бизнес-правила или ограничения бизнес-логики.
 * Оно служит в качестве общего исключения для обработки ошибок, связанных с бизнес-слоем,
 * таких, как попытка зарегистрировать уже существующую сущность, нарушение бизнес-правил
 * или другие условия, определенные бизнес-логикой.
 * <p>
 * Это непроверяемое исключение, что означает, что оно не обязано быть явно заявлено
 * или перехвачено в коде. Оно может использоваться для указания на ошибки в бизнес-логике,
 * которые не всегда могут быть восстановлены, и должно быть обработано на более высоком уровне в приложении.
 * </p>
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
