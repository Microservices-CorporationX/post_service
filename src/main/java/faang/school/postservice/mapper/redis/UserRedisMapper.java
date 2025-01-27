package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.resource.ResourceMapper;
import faang.school.postservice.model.redis.UserRedis;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = ResourceMapper.class)
public interface UserRedisMapper {

    UserRedis toUserRedis(UserDto userDto);

}
