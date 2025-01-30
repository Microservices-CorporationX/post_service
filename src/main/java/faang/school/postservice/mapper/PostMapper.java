package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentForNewsFeedDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostForNewsFeedDto;
import faang.school.postservice.dto.user.UserForNewsFeedResponseDto;
import faang.school.postservice.message.event.PostEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.cache.CommentCache;
import faang.school.postservice.model.cache.PostCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {TreeSet.class, Comparator.class, CommentCache.class})
public interface PostMapper {

    PostDto toDto(Post post);

    List<PostDto> toDto(List<Post> posts);

    Post toEntity(PostDto postDto);

    @Mapping(target = "likeCount", constant = "0L")
    @Mapping(target = "viewCount", constant = "0L")
    @Mapping(
            target = "comments",
            expression = "java(new TreeSet<>(Comparator.comparingLong(CommentCache::getId).reversed()))"
    )
    PostCache toCachedPost(PostDto postDto);

    @Mapping(target = "postId", source = "id")
    PostEvent toPostEventFromPostDto(PostDto postDto);

    @Mapping(target = "user.id", source = "authorId")
    @Mapping(target = "comments", source = "comments", qualifiedByName = "mapCommentsFromCache")
    PostForNewsFeedDto toPostForNewsFeedDto(PostCache postCache);

    @Mapping(target = "user.id", source = "authorId")
    @Mapping(target = "likeCount", expression = "java(post.getLikes().size())")
    @Mapping(target = "comments", source = "comments", qualifiedByName = "mapComments")
    PostForNewsFeedDto toPostForNewsFeedDto(Post post);

    List<PostForNewsFeedDto> toPostForNewsFeedDto(List<Post> posts);

    @Named("mapCommentsFromCache")
    default List<CommentForNewsFeedDto> mapCommentsFromCache(TreeSet<CommentCache> comments) {
        if (comments == null) {
            return new ArrayList<>();
        }

        List<CommentForNewsFeedDto> feed = new ArrayList<>();
        for (var comment : comments) {
            CommentForNewsFeedDto commentDto = new CommentForNewsFeedDto();
            commentDto.setId(comment.getId());
            commentDto.setContent(comment.getContent());
            commentDto.setUser(new UserForNewsFeedResponseDto());
            commentDto.getUser().setId(comment.getAuthorId());
            feed.add(commentDto);
        }
        return feed;
    }

    @Named("mapComments")
    default List<CommentForNewsFeedDto> mapComments(List<Comment> comments) {
        if (comments == null) {
            return new ArrayList<>();
        }

        List<CommentForNewsFeedDto> feed = new ArrayList<>();
        for (var comment : comments) {
            CommentForNewsFeedDto commentDto = new CommentForNewsFeedDto();
            commentDto.setId(comment.getId());
            commentDto.setContent(comment.getContent());
            commentDto.setUser(new UserForNewsFeedResponseDto());
            commentDto.getUser().setId(comment.getAuthorId());
            feed.add(commentDto);
        }
        return feed;
    }
}
