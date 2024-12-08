package faang.school.postservice.schedule;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommenterBanner {
    private final PostService postService;
    @Scheduled(cron = "${schedule.banUser.cron}")
    void banUser(){
        log.info("calling postService.findUserToBan");
        postService.findUserToBan();
    }
}
