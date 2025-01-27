package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.HashtagRequestDto;
import faang.school.postservice.dto.post.HashtagResponseDto;
import faang.school.postservice.mapper.post.HashtagMapper;
import faang.school.postservice.model.post.Hashtag;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.post.HashtagRepository;
import faang.school.postservice.repository.post.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

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

    @Transactional
    @Override
    public void addHashtag(HashtagRequestDto dto) {
        Post post = postRepository.findById(dto.getPostId()).orElseThrow(
                () -> new EntityNotFoundException(String.format("Post not found with id: %d", dto.getPostId())));
        Hashtag hashtag = hashtagRepository.findByName(dto.getHashtag())
                .orElseGet(() -> buildHashtag(dto.getHashtag(), post));
        if (!post.getHashtags().contains(hashtag)) {
            hashtagRepository.addHashtag(post.getId(), hashtag.getId());
        }
    }

    private Hashtag buildHashtag(String hashtag, Post post) {
        return Hashtag.builder()
                .id(UUID.randomUUID())
                .name(hashtag)
                .posts(List.of(post))
                .build();
    }
}
