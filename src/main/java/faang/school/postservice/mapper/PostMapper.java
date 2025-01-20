package faang.school.postservice.mapper;

import faang.school.postservice.dto.posts.PostResultResponse;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
    Post toEntity(PostResultResponse postResultDto);

    PostResultResponse toDto(Post post);
}
