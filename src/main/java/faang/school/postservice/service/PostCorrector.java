package faang.school.postservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class PostCorrector {

    private final PostService postService;

    @Async("my-executor")
    @Scheduled(cron = "${spring.crontab.checkPostsGrammar}")
    public void checkGrammarPosts() {
        try {
            postService.checkGrammarPostContentAndChangeIfNeed();
        } catch (Exception e) {
            log.error("Error occurred while checking grammar posts: ", e);
        }
    }

}
