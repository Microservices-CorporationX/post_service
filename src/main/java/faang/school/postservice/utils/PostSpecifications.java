package faang.school.postservice.utils;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class PostSpecifications {
    public static Specification<Post> isReadyToPublish() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
            return criteriaBuilder.and(
                    criteriaBuilder.isFalse(root.get("published")),
                    criteriaBuilder.isFalse(root.get("deleted")),
                    criteriaBuilder.lessThanOrEqualTo(root.get("scheduledAt"), LocalDateTime.now())
            );
        };
    }
}
