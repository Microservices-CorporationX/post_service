package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import java.util.List;

public interface PostService {
    PostUpdateDto createDraft(PostCreateDto postCreateDto);

    PostUpdateDto publicPost(long id);

    PostUpdateDto updatePost(PostUpdateDto postUpdateDto);

    PostUpdateDto softDeletePost(long id);

    PostUpdateDto getPostById(long id);

    List<PostUpdateDto> getPostDraftsByAuthorId(long id);

    List<PostUpdateDto> getPostDraftsByProjectId(long id);

    List<PostUpdateDto> getPublishedPostsByAuthorId(long id);

    List<PostUpdateDto> getPublishedPostsByProjectId(long id);
}
