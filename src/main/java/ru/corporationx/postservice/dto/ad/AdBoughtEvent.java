package ru.corporationx.postservice.dto.ad;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class AdBoughtEvent {
    private Long id;
    private long post;
    private long buyerId;
    private long appearancesLeft;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
