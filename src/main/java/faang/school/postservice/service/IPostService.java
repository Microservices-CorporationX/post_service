package faang.school.postservice.service;

import faang.school.postservice.model.Post;

import java.util.List;
import java.util.Optional;

public interface IPostService {

    void save(Post post);

    Optional<Post> findById(long id);

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

}
