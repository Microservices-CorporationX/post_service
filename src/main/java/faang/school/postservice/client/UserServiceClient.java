package faang.school.postservice.client;

import faang.school.postservice.dto.filter.UserFilterDto;
import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/api/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @GetMapping("/api/users/list")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @PutMapping("/api/users/{userId}/diactivate")
    UserDto deactivatesUserProfile(@PathVariable Long userId);

    @GetMapping("/api/users/premium")
    List<UserDto> getPremiumUsers(@RequestBody UserFilterDto userFilterDto);

    @GetMapping("/api/users")
    Page<UserDto> getUsers(@RequestParam int page, @RequestParam int size);

    @GetMapping("/api/users/count")
    Long getCountUser();
}
