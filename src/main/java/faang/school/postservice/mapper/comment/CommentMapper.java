package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.like.LikeListMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = LikeListMapper.class)
public interface CommentMapper {

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "mapToLikeId")
    CommentDto toDto(Comment comment);

    @Mapping(target = "post", expression = "java(mapPostId(commentDto.getPostId()))")
    @Mapping(target = "likes", expression = "java(mapLikeIds(commentDto.getLikeIds()))")
    Comment toEntity(CommentDto commentDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void update(CommentDto commentDto, @MappingTarget Comment comment);

    @Named("mapToLikeId")
    default List<Long> mapToLikeId(List<Like> likes) {
        if (likes == null) {
            return new ArrayList<>();
        }
        return likes.stream()
                .map(Like::getId)
                .toList();
    }

    default Post mapPostId(Long postId) {
        return Post.builder()
                .id(postId)
                .build();
    }

    default List<Like> mapLikeIds(List<Long> likeIds) {
        if (likeIds == null) {
            return new ArrayList<>();
        }
        return likeIds.stream()
                .map( likeId -> Like.builder().id(likeId).build())
                .toList();
    }
}

