package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;
    private final PostService postService;

    @Transactional
    public Comment createComment(Comment comment, Long postId) {
        getUser(comment.getAuthorId());
        Post post = postService.getPostById(postId);
        post.getComments().add(comment);
        postService.savePost(post);
        comment.setPost(post);
        return commentRepository.save(comment);
    }

    public Comment updateComment(Comment comment) {
        Comment savedComment = commentRepository.findById(comment.getId())
                .orElseThrow(() -> new NoSuchElementException("Comment not found or deleted"));

        savedComment.setContent(comment.getContent());
        savedComment.setUpdatedAt(LocalDateTime.now());

        return savedComment;
    }

    public List<Comment> getAllCommentsToPost(Long postId) {
        Post post = postService.getPostById(postId);
        return post.getComments().stream()
                .sorted(Comparator.comparing(Comment::getUpdatedAt).reversed())
                .toList();
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private void getUser(Long authorId) {
        try {
            userServiceClient.getUser(authorId);
        } catch (Exception e) {
            throw new NoSuchElementException(String.format("User with ID#%s not found", authorId));
        }
    }
    /*Требования:

Комментарии могут оставлять только конкретные пользователи под любыми постами.

Комментарий можно создать. Он не может быть пустым и длиннее 4096 символов.
У него обязательно должен быть автор — существующий пользователь.
Комментарий обязательно должен относиться к посту. Также у комментария есть дата создания.

Комментарий можно обновить. Если пользователь допустил ошибку в тексте комментария,
то он может изменить его. Все остальные данные изменить нельзя, и это важная проверка для бэкенда.

Можно получить все комментария для конкретного поста по id этого поста.
Комментарии должны быть отсортированы в хронологическом порядке от самого позднего к самому раннему.

Комментарий можно удалить. Пока без проверок, что его может удалить только сам автор.
 Позднее мы добавим все проверки, когда познакомимся с web компонентами, а в частности с сессией пользователя.
     */


}
