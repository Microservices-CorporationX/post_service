package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.HashtagResponseDto;
import faang.school.postservice.mapper.post.HashtagMapper;
import faang.school.postservice.model.post.Hashtag;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.post.HashtagRepository;
import faang.school.postservice.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {
    private final HashtagRepository hashtagRepository;
    private final HashtagMapper hashtagMapper;
    private final PostRepository postRepository;

    @Override
    public List<HashtagResponseDto> getAllHashtags() {
        return hashtagRepository.findAll().stream()
                .map(hashtagMapper::toDto)
                .toList();
    }

    @Cacheable(value = "top_hashtags")
    @Override
    public List<HashtagResponseDto> getTopHashtags() {
        List<Hashtag> allHashtags = hashtagRepository.findAll();
        return allHashtags.stream()
                .sorted(Comparator.comparing(h -> h.getPosts().size(), Comparator.reverseOrder()))
                .limit(5)
                .map(hashtagMapper::toDto)
                .toList();
    }

    @CachePut(key = "#hashtag", value = "postsByHashtag")
    @Override
    public void addHashtag(long postId, String hashtag) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        Hashtag result = hashtagRepository.findByName(hashtag).orElseGet(() -> {
            Hashtag newHashtag = Hashtag.builder()
                    .name(hashtag)
                    .build();
            return hashtagRepository.save(newHashtag);
        });
        List<Hashtag> hashtags = post.getHashtags();
        if (!hashtags.contains(result)) {
            hashtags.add(result);
            post.setHashtags(hashtags);
            postRepository.save(post);
        }
//        return postRepository.findByHashtag(hashtag);

//        if (postRepository.findById(dto.getPostId()).isEmpty()) {
//            throw new EntityNotFoundException(
//                    String.format("Post with id = %s doesn't exists", dto.getPostId()));
//        }
//        if (hashtagRepository.findByName(dto.getHashtag()).isEmpty()) {
//            hashtagRepository.save(Hashtag.builder().name(dto.getHashtag()).build());
//        } else {
//            Hashtag hashtag = hashtagRepository.findByName(dto.getHashtag()).orElseThrow();
//            hashtagRepository.save(hashtag);
//        }
//        hashtagRepository.addHashtag(dto.getPostId(), dto.getHashtag());
    }
}
