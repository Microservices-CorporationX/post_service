package ru.corporationx.postservice.mapper.post;

import ru.corporationx.postservice.dto.post.PostDto;
import ru.corporationx.postservice.model.Like;
import ru.corporationx.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(target = "likesCount", source = "likes")
    PostDto toDto(Post post);

    Post toEntity(PostDto postDto);

    default Long mapLike(List<Like> likes) {
        if (likes == null) {
            return 0L;
        }
        return (long) likes.size();
    }

}