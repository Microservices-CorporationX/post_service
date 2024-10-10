package faang.school.postservice.dto.post.corrector;

import lombok.Builder;

import java.util.List;

@Builder
public record Error(
        String id,
        int offset,
        int length,
        String bad,
        List<String> better,
        String type) {
}
