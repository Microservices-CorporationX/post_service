package ru.corporationx.postservice.controller.ad;

import ru.corporationx.postservice.service.ad.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdController implements AdControllerOas {
    private final AdService adService;

    @PostMapping("/posts/{postId}")
    public void buyAd(@PathVariable long postId) {
        adService.buyAdd(postId);
    }
}
