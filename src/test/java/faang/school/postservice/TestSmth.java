package faang.school.postservice;

import faang.school.postservice.service.feed.cache.HeaterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TestSmth {
    @Autowired
    private HeaterService heaterService;

    @Test
    void test1() throws Exception {
        heaterService.heatUser(1);
        Thread.sleep(3000);
    }
}
