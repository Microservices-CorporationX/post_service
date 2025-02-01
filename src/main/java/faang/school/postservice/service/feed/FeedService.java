package faang.school.postservice.service.feed;

import faang.school.postservice.dto.post.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    public List<PostResponseDto> getUserFeed(Long postId, Long userId) {
        if (postId == null) {
            //достаем из редиса последние 20
        } else {
            //достаем из редиса следующую за postId пачку постов
        }
        return List.of(new PostResponseDto());
    }
}
