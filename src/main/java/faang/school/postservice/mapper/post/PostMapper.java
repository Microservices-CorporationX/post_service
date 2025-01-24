package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Post toEntity(PostDto dto);

    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "mapLikeIds")
    @Mapping(source = "comments", target = "commentIds", qualifiedByName = "mapCommentIds")
    PostDto toDto(Post entity);

    @Named("mapLikeIds")
    default int mapLikeIds(List<Like> likes) {
        return likes.size();
    }

    @Named("mapCommentIds")
    default List<Long> mapCommentIds(List<Comment> comments) {
        return comments.stream().map(Comment::getId).toList();
    }
}
