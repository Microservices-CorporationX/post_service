package faang.school.postservice.service.impl;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    @Override
    public PostDto createDraft(PostDto postDto) {
        return null;
    }
}
