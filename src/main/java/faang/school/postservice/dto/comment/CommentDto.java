package faang.school.postservice.dto.comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CommentDto {

    private long id;
    private String content;
    private long authorId;
    private List<Long> likesId;
    private long postId;
}
