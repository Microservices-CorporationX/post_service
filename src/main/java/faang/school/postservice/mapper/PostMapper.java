package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface PostMapper {
    Post toPostEntity(PostRequestDto postRequestDto);
    Post toPostEntity(PostResponseDto postResponseDto);
    PostRequestDto toPostRequestDto(Post post);
    PostResponseDto toPostResponseDto(Post post);

    List<PostResponseDto> toPostResponseDtos(List<Post> posts);

}
