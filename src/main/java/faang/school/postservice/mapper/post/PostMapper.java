package faang.school.postservice.mapper.post;

import faang.school.postservice.kafka.event.EventPostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "mapLikesCount")
    PostDto toDto(Post post);

    Post toEntity(PostDto postDto);

    Post toEntity(PostRequestDto postRequestDto);

    List<PostDto> toDto(List<Post> posts);

    @Mapping(source = "likes", target = "likes", ignore = true)
    PostCache toCachePost(Post post);

    EventPostDto toEventPostDto(Post post);

    void updatePostFromDto(PostDto postDto, @MappingTarget Post post);

    @Named("mapLikesCount")
    default int mapLikesCount(List<Like> likes) {
        return likes == null ? 0 : likes.size();
    }
}
