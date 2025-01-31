package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.SavePostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    Post toEntity(SavePostDto savePostDto);

    @Mapping(source = "publishedAt", target = "publishedDate")
    PostDto toDto(Post post);

    List<PostDto> toDto(List<Post> posts);
}