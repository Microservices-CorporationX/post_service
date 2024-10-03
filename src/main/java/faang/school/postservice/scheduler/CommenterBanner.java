package faang.school.postservice.scheduler;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommenterBanner {

    private final PostService postService;

    @Scheduled(cron = "${post.comments.scheduler.user-banning}")
    public void banUsers(){
        postService.moderateUserBehaviour();
    }
}
