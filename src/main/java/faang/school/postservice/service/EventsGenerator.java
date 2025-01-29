package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.redis.entity.PostCache;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import faang.school.postservice.redis.repository.PostCacheRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventsGenerator {
    private final PostCacheRedisRepository postCacheRedisRepository;
    private final PostCacheMapper postCacheMapper;

    public void savePostCache(PostDto postDto) {
        PostCache postCache = postCacheMapper.toPostCache(postDto);
        postCacheRedisRepository.save(postCache);
    }


}
