package faang.school.postservice.repository;

import faang.school.postservice.model.entity.UserShortInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserShortInfoRepository extends JpaRepository<UserShortInfo, Long> {

}
