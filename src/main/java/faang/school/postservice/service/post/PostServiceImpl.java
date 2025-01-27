package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Cacheable(key = "#hashtag", value = "postsByHashtag")
    @Override
    public List<PostResponseDto> getPostsByHashtag(String hashtag) {
        List<Post> posts = postRepository.findByHashtag(hashtag);
        return posts.stream()
                .map(postMapper::toDto)
                .toList();
    }
}
