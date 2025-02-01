package faang.school.postservice.client;

import faang.school.postservice.dto.UserFilterDto;
import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/api/user-service/v1/users/{userId}?idRequester=999")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/api/user-service/v1/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @PostMapping("/api/user-service/v1/following/filter/followeeId/{userId}")
    List<UserDto> getFollowingUsers(@PathVariable long userId, @RequestBody UserFilterDto filter);
}
