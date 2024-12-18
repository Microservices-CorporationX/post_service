package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.AdService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/ad")
@Tag(name = "Ad", description = "This controller for buying ads")
public class AdController {
   private final AdService adService;

    @PostMapping("/buy")
    public ResponseEntity<String> buyAd(@RequestParam Long postId,
                                        @RequestParam Long userId,
                                        @RequestParam BigDecimal paymentAmount,
                                        @RequestParam Long adDuration) {
        adService.getUserWhoBuyAd(postId, userId, paymentAmount, adDuration);
        return ResponseEntity.ok("Advertisement purchased successfully");
    }
}
