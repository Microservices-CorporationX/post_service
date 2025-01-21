package faang.school.postservice.util.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.service.UserService;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    public static final long ID = 1L;

    @Mock
    UserServiceClient userServiceClient;

    @InjectMocks
    UserService userService;

    @Test
    public void testUserExist() {
        Mockito.when(userServiceClient.getUser(ID)).thenReturn(new UserDto(ID, "Alex", "email"));

        assertDoesNotThrow(() -> userService.checkUserExists(ID));
    }

    @Test
    public void testUserNotExist() {
        Mockito.when(userServiceClient.getUser(ID)).thenThrow(FeignException.NotFound.class);

        assertThrows(EntityNotFoundException.class, () -> userService.checkUserExists(ID));
    }
}
