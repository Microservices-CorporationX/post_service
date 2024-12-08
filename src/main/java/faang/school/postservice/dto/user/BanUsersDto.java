package faang.school.postservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Builder
@AllArgsConstructor
public record BanUsersDto(List<Long> usersIds) {
}