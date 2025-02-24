package ru.corporationx.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentEvent {
    private long authorId;
    private long postId;
    private long commentId;
    private String content;
}
