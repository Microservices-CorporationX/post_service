package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Test
    public void testGetUserIfUserExists() {
        UserDto userDto = UserDto.builder().build();
        when(userServiceClient.getUser(anyLong())).thenReturn(userDto);

        assertNotNull(userServiceClient.getUser(anyLong()));
    }

    @Test
    public void testGetUserIfUserNotExists() {
        when(userServiceClient.getUser(anyLong())).thenThrow(FeignException.FeignClientException.class);

        assertThrows(FeignException.FeignClientException.class, () -> userServiceClient.getUser(anyLong()));
    }
}