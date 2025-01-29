package faang.school.postservice.mapper;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(source = "likes", target = "likeIds")
    PostDto toDto(Post post);

    @Mapping(source = "likeIds", target = "likes")
    Post toEntity(PostDto postDto);

    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "authorId" , ignore = true)
    @Mapping(target = "projectId" , ignore = true)
    void update(PostDto postDto, @MappingTarget Post post);

    default List<Long> mapLikesToIds(List<Like> likes) {
        if (likes == null) {
            return new ArrayList<>();
        }
        return likes.stream()
                .map(Like::getId)
                .toList();
    }

    default List<Like> mapIdsToLikes(List<Long> likeIds) {
        if (likeIds == null) {
            return new ArrayList<>();
        }
        return likeIds.stream()
                .map(id -> {
                    Like like = new Like();
                    like.setId(id);
                    return like;
                })
                .toList();
    }

    default List<Long> mapCommentsToIds(List<Comment> likes) {
        if (likes == null) {
            return new ArrayList<>();
        }
        return likes.stream()
                .map(Comment::getId)
                .toList();
    }

    default List<Comment> mapIdsToComments(List<Long> commentIds) {
        if (commentIds == null) {
            return new ArrayList<>();
        }
        return commentIds.stream()
                .map(id -> {
                    Comment comment = new Comment();
                    comment.setId(id);
                    return comment;
                })
                .toList();
    }
}
