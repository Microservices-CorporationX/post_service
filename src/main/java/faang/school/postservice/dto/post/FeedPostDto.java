package faang.school.postservice.dto.post;

import faang.school.postservice.dto.comment.FeedCommentDto;
import faang.school.postservice.dto.user.ShortUserWithAvatarDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedPostDto {
    private long id;
    private long authorId;
    private String content;
    private long likes;
    private long views;
    private Long projectId;
    private LocalDateTime publishedAt;
    private ShortUserWithAvatarDto user;
    private List<FeedCommentDto> comments;
}
