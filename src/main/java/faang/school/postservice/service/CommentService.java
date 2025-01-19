package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final PostRepository postRepository;
    private final UserService userService;

    public CommentDto create(long postId, CreateCommentDto createDto) {
        //userService.checkUserExists(createDto.getAuthorId());

        Post post = postService.getPostById(postId);
        List<Comment> comments = post.getComments();

        Comment newComment = commentMapper.toEntity(createDto);
        newComment = commentRepository.save(newComment);

        comments.add(newComment);
        post.setComments(comments);
        postRepository.save(post);

        return commentMapper.toDto(newComment);
    }

    public CommentDto update(long commentId, UpdateCommentDto updateDto) {
        Comment comment = getCommentById(commentId);
        comment.setContent(updateDto.getContent());
        comment.setUpdatedAt(updateDto.getUpdatedAt());

        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    public List<CommentDto> getCommentsByPostId(long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        return comments.stream()
                .map(commentMapper::toDto)
                .toList();
    }

    public void remove(long commentId) {
        commentRepository.deleteById(commentId);
    }

    private Comment getCommentById(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментария с ID " + commentId + " не найден"));
    }

}
