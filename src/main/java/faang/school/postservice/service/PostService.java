package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;

public interface PostService {
    PostDto createDraft(PostDto postDto);

    PostDto publish(Long postId);

    PostDto update(Long id, String content);

    PostDto softDelete(Long id);

    PostDto getById(Long id);
}
