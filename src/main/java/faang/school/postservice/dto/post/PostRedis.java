package faang.school.postservice.dto.post;

import faang.school.postservice.event.PostCommentEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRedis {
    private long id;
    private String title;
    private String content;
    private Long authorId;
    private Long likes;
    private LinkedHashSet<PostCommentEvent> comments;
    private Long views;
}
