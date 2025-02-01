package faang.school.postservice.dto.post;

import faang.school.postservice.dto.comment.CommentFeedDto;
import faang.school.postservice.dto.user.UserNFDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostFeedDto {
    private long id;
    private String content;
    private UserNFDto author;
    private long likes;
    private long views;
    private List<CommentFeedDto> comments;
}
