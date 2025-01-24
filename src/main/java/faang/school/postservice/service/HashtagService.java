package faang.school.postservice.service;

import faang.school.postservice.dto.hashtag.HashtagCreateDto;
import faang.school.postservice.dto.hashtag.HashtagReadDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.HashtagMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagMapper hashtagMapper;
    private final HashtagRepository hashtagRepository;

    public HashtagReadDto create(HashtagCreateDto createDto) {
        //TODO добавить проверку существования Поста
        Hashtag newHashtag = hashtagMapper.toEntity(createDto);
        newHashtag = hashtagRepository.save(newHashtag);
        return hashtagMapper.toDto(newHashtag);
    }

    public HashtagReadDto getHashtag(long hashtagId) {
        return hashtagMapper.toDto(getHashtagById(hashtagId));
    }

    public List<HashtagReadDto> getHashtagsByPostId(long postId) {
        List<Hashtag> hashtags = hashtagRepository.findAllByPostId(postId);

        return hashtags.stream()
                .map(hashtagMapper::toDto)
                .toList();
    }

    public void remove(long hashtagId) {
        hashtagRepository.deleteById(hashtagId);
    }

    public Hashtag getHashtagById(long hashtagId) {
        return hashtagRepository.findById(hashtagId)
                .orElseThrow(() -> new EntityNotFoundException("Хэштэг с ID " + hashtagId + " не найден"));
    }
}
