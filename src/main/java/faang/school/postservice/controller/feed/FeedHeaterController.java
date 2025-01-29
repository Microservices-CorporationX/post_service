package faang.school.postservice.controller.feed;

import faang.school.postservice.service.feed.cache.HeaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feed-heat")
@RequiredArgsConstructor
public class FeedHeaterController {
    private final HeaterService heaterService;

    @PostMapping("/start")
    public void startHeat() {
        heaterService.startHeat();
    }
}
