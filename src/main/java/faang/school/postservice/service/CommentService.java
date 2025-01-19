package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public Comment createComment(Comment comment) {
        if (checkUserIsExists(comment.getAuthorId())) {
            String content = comment.getContent();
            checkContent(content);
            List<Post> posts = postRepository.findByAuthorId(comment.getAuthorId());
        }
    }

    private void checkContent(String content) {
        if (content.isEmpty() || content.length() > 4096) {
            throw new IllegalArgumentException("Message of comment is empty or contains too much characters");
        }
    }

    private boolean checkUserIsExists(Long authorId) {
        return userServiceClient.getUser(authorId).id() > 0;
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
