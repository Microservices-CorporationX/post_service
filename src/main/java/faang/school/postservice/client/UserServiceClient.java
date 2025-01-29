package faang.school.postservice.client;

import faang.school.postservice.dto.user.ShortUserDto;
import faang.school.postservice.dto.user.ShortUserWithAvatarDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserFilterDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/users/with-avatar/{userId}")
    ShortUserWithAvatarDto getShortUserWithAvatarById(@PathVariable long userId);

    @PostMapping("/api/v1/subscription/followers/{followeeId}")
    List<ShortUserDto> getFollowers(@PathVariable long followeeId, @RequestBody UserFilterDto filter);

    @GetMapping("/users/id")
    List<Long> getUserIds(@RequestParam("page") long page, @RequestParam("pageSize") long pageSize);
}
