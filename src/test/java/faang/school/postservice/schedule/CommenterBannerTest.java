package faang.school.postservice.schedule;

import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class CommenterBannerTest {

    @InjectMocks
    private CommenterBanner commenterBanner;

    @Mock
    private PostService postService;

    @Test
    void testCommentBannerCallingPostService(){
        commenterBanner.banUser();

        Mockito.verify(postService).findUserToBan();
    }
}