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
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/ad")
@Tag(name = "Ad", description = "This controller for buying ads")
public class AdController {
   private final AdService adService;

    @PostMapping("/buy/{postId}")
    public ResponseEntity<UserDto> buyAd(@RequestBody @Valid UserDto userDto, @PathVariable long postId) {
       return ResponseEntity.status(HttpStatus.CREATED).body(adService.getUserWhoBuyAd(userDto, postId));
    }
}
