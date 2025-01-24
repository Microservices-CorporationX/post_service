package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment findById(@NotNull Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Коммент с id = %d не найден", commentId))
                );
    }

    public List<Comment> findAllByPostId(@NotNull Long postId){
        return commentRepository.findAllByPostId(postId);
    }

    public boolean existsById(@NotNull Long commentId){
        return commentRepository.existsById(commentId);
    }
}
