package faang.school.postservice.mapper.user;

import faang.school.postservice.cache_entities.AuthorCache;
import faang.school.postservice.dto.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface AuthorCacheMapper {
    @Mapping(source = "id", target = "userId")
    AuthorCache toAuthorCache(UserDto userDto);
    @Mapping(source = "userId", target = "id")
    UserDto toUserDto(AuthorCache authorCache);
}