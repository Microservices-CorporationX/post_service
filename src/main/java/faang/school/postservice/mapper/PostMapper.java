package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "authorId", target = "userId")
    @Mapping(source = "projectId", target = "projectId")
    @Mapping(target = "likesCount", expression = "java(post.getLikes() != null ? post.getLikes().size() : 0)")
    PostDto toDto(Post post);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "userId", target = "authorId")
    @Mapping(source = "projectId", target = "projectId")
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "albums", ignore = true)
    @Mapping(target = "ad", ignore = true)
    @Mapping(target = "resources", ignore = true)
    @Mapping(target = "published", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Post toEntity(PostDto postDto);
}