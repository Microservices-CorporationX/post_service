package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.mapper.UserWithFollowersMapper;
import faang.school.postservice.model.dto.UserWithFollowersDto;
import faang.school.postservice.model.entity.UserShortInfo;
import faang.school.postservice.repository.UserShortInfoRepository;
import faang.school.postservice.service.UserShortInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserShortInfoServiceImpl implements UserShortInfoService {
    @Value("${system-user-id}")
    private int systemUserId;
    private final UserShortInfoRepository userShortInfoRepository;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final UserWithFollowersMapper userWithFollowersMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public UserShortInfo updateUserShortInfoIfStale(Long userId, int refreshTime) {
        userContext.setUserId(systemUserId);

        return userShortInfoRepository.findById(userId)
                .filter(info -> info.getLastSavedAt().isAfter(LocalDateTime.now().minusHours(refreshTime)))
                .orElseGet(() -> {
                    UserWithFollowersDto userWithFollowers = userServiceClient.getUserWithFollowers(userId);
                    UserShortInfo newUserShortInfo = userWithFollowersMapper.toUserShortInfo(userWithFollowers);
                    return userShortInfoRepository.save(newUserShortInfo);
                });
    }
}
