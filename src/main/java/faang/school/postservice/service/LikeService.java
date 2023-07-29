package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeValidator likeValidator;
    private final PostRepository postRepository;
    private final LikeMapper likeMapper;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    public LikeDto likePost(long postId, LikeDto likeDto) {

        likeValidator.validateLike(likeDto);
        Optional<Post> post = postRepository.findById(postId);
        Like like = likeMapper.toModel(likeDto);

        post.map(p -> {
            like.setPost(p);
            likeRepository.save(like);
            return p;
        });
        return likeMapper.toDto(like);
    }

    public void unlikePost(long postId, long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    public LikeDto likeComment(long commentId, LikeDto likeDto) {

        likeValidator.validateLike(likeDto);
        Optional<Comment> comment = commentRepository.findById(commentId);
        Like like = likeMapper.toModel(likeDto);

        comment.map(c -> {
            like.setComment(c);
            likeRepository.save(like);
            return c;
        });
        return likeMapper.toDto(like);
    }

    public void unlikeComment(long commentId, long userId) {
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }
}
