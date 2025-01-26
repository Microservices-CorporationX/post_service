package faang.school.postservice.client;

import faang.school.postservice.dto.user.ShortUserDto;
import faang.school.postservice.dto.user.ShortUserWithAvatarDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserFilterDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/users/with-avatar/{userId}")
    ShortUserWithAvatarDto getShortUserWithAvatarById(@PathVariable long userId);

    @PostMapping("followers/{followeeId}")
    List<ShortUserDto> getFollowers(@PathVariable long followeeId, @RequestBody UserFilterDto filter);
}
