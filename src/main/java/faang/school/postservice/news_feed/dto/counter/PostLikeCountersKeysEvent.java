package faang.school.postservice.news_feed.dto.counter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostLikeCountersKeysEvent {
    private List<String> likeCountersKeys;
}

