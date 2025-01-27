package faang.school.postservice.model.post;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "hashtag")
public class Hashtag {

    @Id
    private UUID id;

    private String name;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "post_hashtag",
            joinColumns = @JoinColumn(name = "hashtag_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private List<Post> posts;
}
