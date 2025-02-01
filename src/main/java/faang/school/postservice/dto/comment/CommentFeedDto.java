package faang.school.postservice.dto.comment;

import faang.school.postservice.dto.user.UserNFDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentFeedDto {
    private long id;
    private String content;
    private UserNFDto author;
    private LocalDateTime createdAt;
}
