package faang.school.postservice.service;

import faang.school.postservice.model.entity.UserShortInfo;

public interface UserShortInfoService {
    UserShortInfo updateUserShortInfoIfStale(Long userId, int refreshTime);
}
