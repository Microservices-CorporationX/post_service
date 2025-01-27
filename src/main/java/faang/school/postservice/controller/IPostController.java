package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController("/post")
public interface IPostController {

    /**
     * сохраняет данный объект поста в базу данных или
     * обновляет уже имеющийся пост в базе данных, id которого
     * совпадает с id переданного объекта в этот метод
     * @param post
     */
    @PostMapping("/post")
    void save(PostDto post);

    /**
     * Находит в базе данных строку с постом с переданным в метод id.
     * Возвращает Optional потому, что в БД может и не оказаться
     * строки с таким id, если мы ищем несуществующий пост
     * @param id
     * @return пост
     */
    @GetMapping("findById")
    Optional<PostDto> findById(long id);

    /**
     * возвращает все посты в БД, у которых автором является
     * пользователь с id, совпадающим с authorId, переданным в метод;
     * @param authorId
     * @return список постов
     */
    @GetMapping("findByAuthor")
    List<PostDto> findByAuthorId(long authorId);

    /**
     *
     * @param projectId
     * @return
     */
    @GetMapping("findByProject")
    List<PostDto> findByProjectId(long projectId);
}
