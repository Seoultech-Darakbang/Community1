package darak.community.repository;

import darak.community.domain.Gifticon;
import darak.community.domain.GifticonStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GifticonRepository extends JpaRepository<Gifticon, Long> {
    
    List<Gifticon> findByStatusOrderByCreatedDateDesc(GifticonStatus status);
    
    @Query("SELECT g FROM Gifticon g WHERE g.status = :status " +
           "AND g.startTime <= :now AND g.endTime > :now " +
           "ORDER BY g.createdDate DESC")
    List<Gifticon> findActiveGifticons(@Param("status") GifticonStatus status, 
                                      @Param("now") LocalDateTime now);
    
    Page<Gifticon> findAllByOrderByCreatedDateDesc(Pageable pageable);
    
    // 비관적 락을 사용한 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM Gifticon g WHERE g.id = :id")
    Optional<Gifticon> findByIdWithLock(@Param("id") Long id);
} 