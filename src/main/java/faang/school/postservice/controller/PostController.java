package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.service.PostService;

import java.util.List;
import java.util.Optional;

public class PostController implements IPostController {
    PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    @Override
    public void save(PostDto post) {
        service.save(PostMapper.INSTANCE.fromDto(post));
    }

    @Override
    public Optional<PostDto> findById(long id) {
        return service.findById(id).map(PostMapper.INSTANCE::toDto);
    }

    @Override
    public List<PostDto> findByAuthorId(long authorId) {
        return PostMapper.INSTANCE.toDto(service.findByAuthorId(authorId));
    }

    @Override
    public List<PostDto> findByProjectId(long projectId) {
        return PostMapper.INSTANCE.toDto(service.findByProjectId(projectId));
    }
}
