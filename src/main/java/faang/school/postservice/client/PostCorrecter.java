package faang.school.postservice.client;

import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCorrecter {

    private final PostService postService;

    @Scheduled(cron = "${daily.cron.check-posts}")
    public void checkPosts(){
        postService.checkPostsForErrors();
    }
}
