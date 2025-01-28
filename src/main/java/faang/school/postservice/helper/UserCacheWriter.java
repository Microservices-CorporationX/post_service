package faang.school.postservice.helper;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.ShortUserWithAvatarDto;
import faang.school.postservice.model.user.ShortUserWithAvatar;
import faang.school.postservice.repository.user.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class UserCacheWriter {
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final UserCacheRepository userCacheRepository;

    public void cacheUser(long userId) {
        userContext.setUserId(1L);
        ShortUserWithAvatarDto userDto = getShortUserWithAvatarDtoFromUserService(userId);
        ShortUserWithAvatar user = ShortUserWithAvatar
                .builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .smallAvatarId(userDto.getSmallAvatarId())
                .build();
        log.info("Saving user {} to cache", user);
        userCacheRepository.save(user);
    }

    @Retryable(maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    private ShortUserWithAvatarDto getShortUserWithAvatarDtoFromUserService(long userId) {
        try {
            return userServiceClient.getShortUserWithAvatarById(userId);
        } catch (Exception e) {
            log.error("Error when getting user from UserService", e);
            throw e;
        }
    }
}
