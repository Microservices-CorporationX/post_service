package faang.school.postservice.service.comment;

import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.event.CommentPostEvent;
import faang.school.postservice.publisher.PostCommentEventPublisher;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentEventService {

    private final PostRepository postRepository;
    private final PostCommentEventPublisher commentEventPublisher;

    public void handleCommentEvent(Long postId, Comment commentApply) {
        var post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new ResourceNotFoundException("NO OBJECT PRESENT WITH ID = " + postId);
        }
        if (!post.get().getAuthorId().equals(commentApply.getAuthorId())) {
            CommentPostEvent event = new CommentPostEvent(
                    postId,
                    commentApply.getAuthorId(),
                    commentApply.getId(),
                    LocalDateTime.now()
            );
            commentEventPublisher.publishCommentEvent(event);
        }
    }
}


