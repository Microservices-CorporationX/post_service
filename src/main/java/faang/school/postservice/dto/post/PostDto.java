package faang.school.postservice.dto.post;

public record PostDto(
        Long id,
        String content,
        Long authorId,
        Long projectId
) {
}
