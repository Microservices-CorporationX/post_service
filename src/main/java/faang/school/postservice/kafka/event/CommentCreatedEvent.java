package faang.school.postservice.kafka.event;

import faang.school.postservice.dto.comment.CacheCommentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreatedEvent {

    private CacheCommentDto cacheCommentDto;
}
