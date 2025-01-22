package faang.school.postservice.service.banner;

import faang.school.postservice.dto.user.message.UsersForBanMessage;
import faang.school.postservice.message.broker.publisher.user.UsersBanMessageToKafkaPublisher;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorBannerTest {
    @Mock
    private PostService postService;

    @Mock
    private UsersBanMessageToKafkaPublisher userBanMessagePublisher;

    @InjectMocks
    private AuthorBanner authorBanner;

    @Test
    void publishingUsersForBan() {
        List<Long> userIds = List.of(1L, 2L, 5L, 11L, 25L, 12L);
        when(postService.findUserIdsForBan()).thenReturn(userIds);

        authorBanner.publishingUsersForBan();

        ArgumentCaptor<UsersForBanMessage> captor = ArgumentCaptor.forClass(UsersForBanMessage.class);

        verify(userBanMessagePublisher).publish(captor.capture());

        assertThat(captor.getValue().getUserIds())
                .isEqualTo(userIds);
    }

    @Test
    void testPublishingUsersForBan_emptyUserList() {
        when(postService.findUserIdsForBan()).thenReturn(List.of());
        authorBanner.publishingUsersForBan();
        verify(userBanMessagePublisher, never()).publish(any(UsersForBanMessage.class));
    }
}