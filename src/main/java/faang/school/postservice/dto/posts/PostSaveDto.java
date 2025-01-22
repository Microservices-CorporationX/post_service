package faang.school.postservice.dto.posts;

import lombok.Getter;

@Getter
public class PostSaveDto {
    private Long authorId;
    private Long projectId;
    private String content;
}
