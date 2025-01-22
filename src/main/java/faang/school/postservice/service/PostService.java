package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;

public interface PostService {
    PostDto createDraft(PostDto postDto);
}
