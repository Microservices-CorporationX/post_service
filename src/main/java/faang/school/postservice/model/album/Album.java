package faang.school.postservice.model.album;

import faang.school.postservice.model.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "album")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", nullable = false, length = 256)
    private String title;

    @Column(name = "description", nullable = false, length = 4096)
    private String description;

    @Column(name = "author_id", nullable = false)
    private long authorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private AlbumVisibility visibility;

    @ManyToMany
    @JoinTable(name = "post_album", joinColumns = @JoinColumn(name = "album_id"), inverseJoinColumns = @JoinColumn(name = "post_id"))
    private List<Post> posts;

    @ElementCollection
    @CollectionTable(name = "album_users", joinColumns = @JoinColumn(name = "album_id"))
    @Column(name = "user_id")
    private List<Long> favouriteUserIds;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void addPost(Post post) {
        posts.add(post);
    }

    public void removePost(long postId) {
        posts.removeIf(post -> post.getId() == postId);
    }
}
