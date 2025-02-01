package faang.school.postservice.dto.post;

import faang.school.postservice.dto.comment.CommentRedis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRedis {
    private long id;
    private String content;
    private long authorId;
    private Long likes = 0L;
    private List<CommentRedis> comments = new ArrayList<>();
    private Long views = 0L;
}
