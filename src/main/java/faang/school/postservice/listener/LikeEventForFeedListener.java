package faang.school.postservice.listener;

import com.google.protobuf.InvalidProtocolBufferException;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.protobuf.generate.FeedEventProto;
import faang.school.postservice.repository.CacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventForFeedListener implements KafkaEventListener<byte[]> {

    private final CacheRepository<LikeDto> likeCache;
    private final LikeMapper likeMapper;

    @Override
    public void onMessage(byte[] byteEvent, Acknowledgment acknowledgment) {
        try {
            FeedEventProto.FeedEvent feedEvent = FeedEventProto.FeedEvent.parseFrom(byteEvent);
            LikeDto likeDto = likeMapper.toLikeDto(feedEvent);
            String stringPostId = likeDto.getPostId().toString();
            likeCache.save(stringPostId, likeDto);

        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }
}
