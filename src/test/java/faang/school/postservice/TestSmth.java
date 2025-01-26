package faang.school.postservice;

import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestSmth {
    @Autowired
    private PostService postService;

    @Test
    void test1() throws Exception {
        postService.publish(14);
        Thread.sleep(3000);
    }
}
