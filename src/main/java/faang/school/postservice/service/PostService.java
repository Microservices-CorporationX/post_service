package faang.school.postservice.service;

import faang.school.postservice.dto.posts.PostDto;
import faang.school.postservice.dto.posts.PostSaveDto;

import java.util.List;

public interface PostService {
    PostDto create(PostSaveDto postSaveDto);

    PostDto getPost(long id);

    PostDto update(long id, PostSaveDto postSaveDto);

    void publish(long id);

    void delete(long id);

    List<PostDto> getPostsByAuthorId(long id, boolean published);

    List<PostDto> getPostsByProjectId(long id, boolean published);

    int publishingPostsOnSchedule();
}
