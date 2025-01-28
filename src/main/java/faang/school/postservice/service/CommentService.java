package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserServiceClient userService;

    public Comment createComment(Comment comment, Long postId, Long authorId) {
        Post post = postService.getPostById(postId);
        UserDto user = userService.getUser(authorId);

        if (post == null) {
            throw new IllegalArgumentException("There is no post with id: " + postId);
        }

        if (user == null) {
            throw new IllegalArgumentException("There is no user with id: " + authorId);
        }

        comment.setPost(post);

        Comment created = commentRepository.save(comment);

        return created;
    }
}
