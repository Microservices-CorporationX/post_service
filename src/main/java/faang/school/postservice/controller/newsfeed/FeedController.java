package faang.school.postservice.controller.newsfeed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.newsfeed.FeedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    public final UserContext userContext;

//    @GetMapping
//    public ResponseEntity<List<FeedDto>> getFeed(@RequestParam(value = "postId", required = false) Long postId){
//        Long currentUser = userContext.getUserId();
//        return ""
//    }
}
