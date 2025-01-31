package faang.school.postservice.mapper.ad;

import faang.school.postservice.dto.ad.AdBoughtEvent;
import faang.school.postservice.dto.ad.AdDto;
import faang.school.postservice.model.entity.Ad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;

import static java.time.Duration.between;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdMapper {

    @Mapping(target = "post.id", source = "postId")
    Ad toEntity(AdDto adDto);

    @Mapping(target = "postId", source = "post.id")
    AdDto toDto(Ad ad);

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "userId", source = "buyerId")
    @Mapping(target = "paymentAmount", source = "price")
    @Mapping(target = "adDuration", expression = "java(calculateAdDuration(ad.getStartDate(), ad.getEndDate()))")
    @Mapping(target = "boughtAt", expression = "java(java.time.LocalDateTime.now())")
    AdBoughtEvent toEvent(Ad ad);

    default int calculateAdDuration(LocalDateTime startDate, LocalDateTime endDate) {
        return (int) between(startDate, endDate).toDays();
    }
}