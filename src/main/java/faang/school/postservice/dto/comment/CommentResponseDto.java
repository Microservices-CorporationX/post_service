package faang.school.postservice.dto.comment;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponseDto(
        Long id,
        String content,
        Long authorId,
        List<LikeDto> likeDtos,
        PostDto postDto,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {  }
