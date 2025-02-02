package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.news_feed_models.NewsFeedPost;
import faang.school.postservice.dto.post.PostResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NewsFeedPostMapper {
    @Mapping(source = "id", target = "postId")
    @Mapping(target = "countViews", ignore = true)
    NewsFeedPost toCache(PostResponseDto postResponseDto);

    @Mapping(source = "postId", target = "id")
    @Mapping(target = "likeIds", ignore = true)
    @Mapping(target = "commentIds", ignore = true)
    @Mapping(target = "scheduledAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "audio", ignore = true)
    PostResponseDto toResponseDto(NewsFeedPost newsFeedPost);
}