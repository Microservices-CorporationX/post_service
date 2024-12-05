package faang.school.postservice.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.dto.redis.cache.RedisUserDto;
import faang.school.postservice.model.entity.UserShortInfo;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserShortInfoMapper {

    @Autowired
    private ObjectMapper objectMapper;

    @Mapping(source = "followerIds", target = "followerIds", qualifiedByName = "deserializeFollowerIds")
    public abstract RedisUserDto toRedisUserDto(UserShortInfo userShortInfo);

    @Named("deserializeFollowerIds")
    protected List<Long> deserializeFollowerIds(String followerIds) {
        if (followerIds == null || followerIds.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(followerIds, new TypeReference<List<Long>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("Failed to deserialize followerIds %s", followerIds), e);
        }
    }

    @AfterMapping
    protected void setUpdatedAt(@MappingTarget RedisUserDto redisUserDto) {
        redisUserDto.setUpdatedAt(LocalDateTime.now());
    }

}