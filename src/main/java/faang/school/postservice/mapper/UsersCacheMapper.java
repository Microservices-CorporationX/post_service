package faang.school.postservice.mapper;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.redis.CacheUser;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsersCacheMapper {
    CacheUser toCacheUser(UserDto userDto);
}
