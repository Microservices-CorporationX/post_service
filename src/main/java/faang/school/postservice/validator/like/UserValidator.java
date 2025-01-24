package faang.school.postservice.validator.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserServiceClient userServiceClient;

    public void validateUser(Long userId) {
        if (userId == null) {
            throw new DataValidationException("Id юзера не может быть равно null");
        }
        userServiceClient.getUser(userId);
    }
}
