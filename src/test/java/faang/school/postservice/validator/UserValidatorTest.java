package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    public void validateUserIdWithStatus400FailTest() {
        long userId = 1L;
        when(userServiceClient.getUser(userId)).thenThrow(new FeignException(400, "message") {
        });


        EntityNotFoundException entityNotFoundException =
                assertThrows(EntityNotFoundException.class,
                        () -> userValidator.validateUserId(userId)
                );
        verify(userServiceClient, times(1)).getUser(userId);
        assertTrue(entityNotFoundException.getMessage().contains(String.format(UserValidator.USER_NOT_FOUND_BY_ID, userId, "")));
    }

    @Test
    public void validateUserIdWithStatus500FailTest() {
        long userId = 1L;
        when(userServiceClient.getUser(userId)).thenThrow(new FeignException(500, "message") {
        });


        RuntimeException runtimeException =
                assertThrows(RuntimeException.class,
                        () -> userValidator.validateUserId(userId)

                );
        verify(userServiceClient, times(1)).getUser(userId);
        assertTrue(runtimeException.getMessage().contains(String.format(UserValidator.INTERNAL_SERVER_ERROR, userId, "")));
    }

    @Test
    public void validateUserIdSuccessTest() {
        long userId = 1L;
        when(userServiceClient.getUser(userId)).thenReturn(any());

        userValidator.validateUserId(userId);

        verify(userServiceClient, times(1)).getUser(userId);
    }
}
