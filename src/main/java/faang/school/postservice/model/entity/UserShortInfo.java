package faang.school.postservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_short_info")
public class UserShortInfo {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, length = 64)
    private String username;

    @Column(name = "file_id")
    private String fileId;

    @Column(name = "small_file_id")
    private String smallFileId;

    @Column(name = "follower_ids", columnDefinition = "TEXT")
    private String followerIds;

    @Column(name = "last_saved_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime lastSavedAt;

    public UserShortInfo(Long userId, String username, String fileId, String smallFileId, String followerIds) {
        this.userId = userId;
        this.username = username;
        this.fileId = fileId;
        this.smallFileId = smallFileId;
        this.followerIds = followerIds;
    }
}


