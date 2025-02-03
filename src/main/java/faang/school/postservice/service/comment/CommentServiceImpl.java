package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentFiltersDto;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;

    @Override
    public CommentResponseDto createComment(CommentRequestDto commentDto) {
        UserDto userDto = getUser(commentDto);
        Post post = getPostById(commentDto.postId());
        Comment comment = commentMapper.toCommentEntity(commentDto);
        comment.setPost(post);
        Comment SavedComment = commentRepository.save(comment);
        CommentResponseDto commentResponseDto = commentMapper.toCommentResponseDto(SavedComment);
        //return commentMapper.toCommentResponseDto(commentRepository.save(comment));
        return commentResponseDto;
    }

    @Override
    public CommentResponseDto updateComment(long commentId, long authorId, CommentUpdateDto commentUpdateDto) {
        Comment foundComment = getById(commentId);
        if (!foundComment.getAuthorId().equals(authorId)) {
            throw new IllegalArgumentException(String.format("User with id %s is not allowed to update this comment.",
                    authorId));
        }
        foundComment.setContent(commentUpdateDto.content());
        Comment SavedComment = commentRepository.save(foundComment);
        CommentResponseDto commentResponseDto = commentMapper.toCommentResponseDto(SavedComment);
        //return commentMapper.toCommentResponseDto(commentRepository.save(foundComment));
        return commentResponseDto;
    }

    @Override
    public List<CommentResponseDto> getComments(CommentFiltersDto commentFiltersDto) {
        return commentRepository.findAllByPostId(commentFiltersDto.postId())
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::toCommentResponseDto)
                .toList();
    }

    @Override
    public void deleteComment(long commentId) {
        getById(commentId);
        commentRepository.deleteById(commentId);
    }

    private Comment getById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(String.format("Comment with id %d not found", id))
                );
    }

    private Post getPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(()
                        -> new IllegalArgumentException(String.format("Post with id %s not found.", postId))
                );
    }

    private UserDto getUser(CommentRequestDto commentDto) {
        UserDto user = userServiceClient.getUser(commentDto.authorId());
        /* Long userId = commentDto.authorId();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<UserDto> response = restTemplate.exchange("http://localhost:8080/users/" + userId,
                HttpMethod.GET, null, UserDto.class);
        UserDto user = response.getBody(); */
        if (user == null) {
            throw new IllegalArgumentException(String.format("User with id %s not found", commentDto.authorId()));
        }
        return user;
    }
}
