package faang.school.postservice.model.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@Builder
@ToString
public class CommentCache implements Comparable<CommentCache>, Serializable {
    private Long id;
    private String content;
    private Long authorId;

    @Override
    public int compareTo(CommentCache o) {
        return Long.compare(o.id, id);
    }
}
