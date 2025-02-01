package faang.school.postservice.mapper.user;

import faang.school.postservice.dto.news_feed_models.NewsFeedAuthor;
import faang.school.postservice.dto.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface AuthorCacheMapper {
    @Mapping(source = "id", target = "userId")
    NewsFeedAuthor toAuthorCache(UserDto userDto);
    @Mapping(source = "userId", target = "id")
    UserDto toUserDto(NewsFeedAuthor newsFeedAuthor);
}