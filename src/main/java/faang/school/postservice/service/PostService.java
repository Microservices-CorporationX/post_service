package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.SavePostDto;

import java.util.List;

public interface PostService {

    PostDto create(SavePostDto postSaveDto);

    PostDto getPost(long id);

    PostDto update(long id, SavePostDto postSaveDto);

    PostDto publish(long id);

    PostDto delete(long id);

    List<PostDto> getDraftPostsByAuthorId(long authorId);

    List<PostDto> getPublishedPostsByAuthorId(long authorId);

    List<PostDto> getDraftPostsByProjectId(long projectId);

    List<PostDto> getPublishedPostsByProjectId(long projectId);

}
