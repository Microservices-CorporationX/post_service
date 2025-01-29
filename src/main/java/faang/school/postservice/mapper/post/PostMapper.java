package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.FeedPostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(target = "likesCount", source = "likes")
    PostDto toDto(Post post);

    Post toEntity(PostDto postDto);

    FeedPostDto cacheToFeedPostDto(PostCache postCache);

    @Mapping(target = "comments", ignore = true)
    FeedPostDto toFeedPostDto(Post post);

    default Long mapLike(List<Like> likes) {
        if (likes == null) {
            return 0L;
        }
        return (long) likes.size();
    }

}