package ru.corporationx.postservice.scheduler.post;

import ru.corporationx.postservice.model.Post;
import ru.corporationx.postservice.repository.PostRepository;
import ru.corporationx.postservice.service.post.ModerationDictionary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModerationScheduler {

    @Value("${moderation.batch-size}")
    private int moderationBachSize;
    private final PostRepository postRepository;
    private final ModerationDictionary moderationDictionary;

    @Async("moderationPostThreadPool")
    @Transactional
    @Scheduled(cron = "${moderation.cron}", zone = "${moderation.zone}")
    public void moderatePostToOffensiveContent() {
        log.info("moderation to offensive content is starting");
        List<Post> notCheckedToVerificationPosts = new ArrayList<>();
        postRepository.findNotCheckedToVerificationPosts(LocalDateTime.now().minusDays(1L))
                .ifPresentOrElse(
                        posts -> notCheckedToVerificationPosts.addAll(posts),
                        () -> log.info("not checked posts to offensive content not found!")
                );
        if (notCheckedToVerificationPosts.isEmpty()) {
            log.info("not checked posts not found!");
            return;
        }

        List<CompletableFuture<Void>> verifiedPostsFuture = ListUtils
                .partition(notCheckedToVerificationPosts, moderationBachSize).stream()
                .map(this::checkToOffensive)
                .toList();

        CompletableFuture.allOf(verifiedPostsFuture.toArray(new CompletableFuture[0]))
                .join();
        log.info("moderation to offensive content finished, checked posts number: {}",
                notCheckedToVerificationPosts.size());
    }

    public CompletableFuture<Void> checkToOffensive(List<Post> posts) {
        return CompletableFuture.runAsync(() -> {
            posts.stream()
                    .peek(post -> {
                        post.setVerified(true);
                        post.setVerifiedDate(LocalDateTime.now());
                    })
                    .filter(post -> moderationDictionary.containsProfanity(post.getContent()))
                    .forEach(post -> {
                        post.setVerifiedDate(LocalDateTime.now());
                        post.setVerified(false);
                    });
        });
    }
}
