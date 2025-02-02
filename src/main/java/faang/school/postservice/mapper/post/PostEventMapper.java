package faang.school.postservice.mapper.post;

import faang.school.postservice.mapper.resource.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.cache.PostEvent;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = ResourceMapper.class)
public interface PostEventMapper {

    PostEvent toEvent(Post post);
}
