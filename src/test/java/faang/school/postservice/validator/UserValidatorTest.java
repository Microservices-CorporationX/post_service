package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {
    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private UserValidator userValidator;

    private AlbumDto albumDto;

    @BeforeEach
    void setUp() {
        albumDto = AlbumDto.builder()
                .title("Album1")
                .description("album about spring")
                .authorId(1)
                .build();
    }

    @Test
    void checkUserExistenceWhenUserExists() {
        when(userServiceClient.getUser(albumDto.getAuthorId()))
                .thenReturn(new UserDto(1L, "User", "email"));

        assertDoesNotThrow(() -> userValidator.checkUserExistence(albumDto.getAuthorId()),
                "No exception should be thrown if the user exists");
        verify(userServiceClient, times(1)).getUser(albumDto.getAuthorId());
    }

    @Test
    void checkUserExistenceWhenUserNotFound() {
        when(userServiceClient.getUser(albumDto.getAuthorId()))
                .thenThrow(new EntityNotFoundException("User not found with id: " + albumDto.getAuthorId()));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userValidator.checkUserExistence(albumDto.getAuthorId());
        });
        verify(userServiceClient, times(1)).getUser(albumDto.getAuthorId());
    }
}
