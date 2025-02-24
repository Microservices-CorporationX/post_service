package ru.corporationx.postservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Builder
public record BanUsersDto(List<Long> usersIds) {
}