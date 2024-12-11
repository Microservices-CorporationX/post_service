package faang.school.postservice.service;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class PostVerificationService {
    private final PostRepository postRepository;
    private final ModerationDictionary moderationDictionary;

    @Transactional
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Void> checkAndVerifyPostsInBatch(List<Post> postsToVerify) {
        for (Post post : postsToVerify) {
            if (moderationDictionary.containsForbiddenWord(post.getContent())) {
                post.setVerified(false);
            } else {
                post.setVerified(true);
                post.setVerifiedDate(LocalDateTime.now());
            }
            postRepository.save(post);
        }
        return CompletableFuture.completedFuture(null);
    }
}
