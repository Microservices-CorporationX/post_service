package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;

import java.util.List;

public interface PostService {

    PostResponseDto create(CreatePostDto postSaveDto);

    PostResponseDto getPost(long id);

    PostResponseDto update(long id, UpdatePostDto updatePostDto);

    PostResponseDto publish(long id);

    PostResponseDto delete(long id);

    List<PostResponseDto> getDraftPostsByAuthorId(long authorId);

    List<PostResponseDto> getPublishedPostsByAuthorId(long authorId);

    List<PostResponseDto> getDraftPostsByProjectId(long projectId);

    List<PostResponseDto> getPublishedPostsByProjectId(long projectId);
}
