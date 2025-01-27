package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;

import java.util.List;
import java.util.Optional;

public class PostService implements IPostService {
    PostRepository repository;
    UserServiceClient userServiceClient;
    ProjectServiceClient projectServiceClient;
    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Post post) {
        repository.save(post);
    }

    @Override
    public Optional<Post> findById(long id) {
        return repository.findById(id);
    }

    @Override
    public List<Post> findByAuthorId(long authorId) {
        userServiceClient.getUser(authorId);
        return repository.findByAuthorId(authorId);
    }

    @Override
    public List<Post> findByProjectId(long projectId) {
        projectServiceClient.getProject(projectId);
        return repository.findByProjectId(projectId);
    }
}
