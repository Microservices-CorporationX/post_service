package faang.school.postservice.dto.comment;


import java.time.LocalDateTime;

public class CommentDto {
    private Long id;
    private Long authorId;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
