package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserExtendedFilterDto;
import faang.school.postservice.dto.user.UserResponseShortDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Primary
@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @PostMapping("/subscriptions/{followeeId}/followers")
    List<UserResponseShortDto> getFollowers(@PathVariable long followeeId, @RequestBody UserExtendedFilterDto filter);

    @PostMapping("/users/active")
    List<Long> getOnlyActiveUsersFromList(@RequestBody List<Long> ids);
}
