package faang.school.postservice.repository;

import faang.school.postservice.model.OutboxEvent;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByProcessedFalse();

    @Modifying
    @Transactional
    @Query("DELETE FROM OutboxEvent e WHERE e.processed = true")
    int deleteProcessedEvents();
}
