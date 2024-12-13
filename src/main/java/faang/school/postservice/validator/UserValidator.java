package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserValidator {
    private UserServiceClient userServiceClient;

    @Autowired
    public UserValidator(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    public void checkUserExistence(Long authorId) {
        try {
            userServiceClient.getUser(authorId);
        } catch (FeignException.NotFound e) {
            log.error("Feign exception occurred: ", e);
            throw new EntityNotFoundException("User not found with id: " + authorId);
        }
    }
}
