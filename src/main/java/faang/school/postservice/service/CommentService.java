package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment getComment(long id) {
        log.info("getting comment from db");
        return commentRepository.findById(id).orElseThrow(
                () ->new EntityNotFoundException(String.format("Comment %d not found", id)));
    }

    public boolean existsCommentById(Long id) {
        log.info("checking comment for existence");
        if (id == null) return false;
        return commentRepository.existsById(id);
    }

    public List<Comment> getAllNotVerifiedComments(){
        log.info("getting not verified comments");
        return commentRepository.getAllNotVerified();
    }

    public void markAsRemovedUnVerifiedComments(){
        commentRepository.markAsRemovedAllUnVerifiedComments();
    }
}
