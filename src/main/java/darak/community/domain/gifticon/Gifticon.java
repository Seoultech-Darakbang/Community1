package darak.community.domain.gifticon;

import darak.community.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Gifticon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gifticon_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = true)
    private String imageUrl;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Integer remainingQuantity;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GifticonStatus status;

    @Builder
    public Gifticon(String title, String description, String imageUrl, String brand,
                    Integer totalQuantity, LocalDateTime startTime, LocalDateTime endTime) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.brand = brand;
        this.totalQuantity = totalQuantity;

        if (totalQuantity == null) {
            throw new IllegalArgumentException("totalQuantity는 null일 수 없습니다.");
        }

        this.remainingQuantity = totalQuantity;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = GifticonStatus.WAITING;
    }

    public boolean canClaim() {
        LocalDateTime now = LocalDateTime.now();
        return status == GifticonStatus.ACTIVE &&
                remainingQuantity > 0 &&
                now.isAfter(startTime) &&
                now.isBefore(endTime);
    }

    public void claimOne() {
        if (!canClaim()) {
            throw new IllegalStateException("기프티콘을 받을 수 없는 상태입니다.");
        }
        this.remainingQuantity--;
        if (this.remainingQuantity == 0) {
            this.status = GifticonStatus.SOLD_OUT;
        }
    }

    public void activate() {
        this.status = GifticonStatus.ACTIVE;
    }

    public void close() {
        this.status = GifticonStatus.CLOSED;
    }
} 