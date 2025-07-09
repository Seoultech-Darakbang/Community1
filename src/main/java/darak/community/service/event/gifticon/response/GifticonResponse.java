package darak.community.service.event.gifticon.response;

import darak.community.domain.gifticon.Gifticon;
import darak.community.domain.gifticon.GifticonStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GifticonResponse {

    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String brand;
    private Integer totalQuantity;
    private Integer remainingQuantity;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private GifticonStatus status;
    private boolean canClaim;
    private LocalDateTime createdDate;

    @Builder
    private GifticonResponse(Long id, String title, String description, String imageUrl, String brand,
                             Integer totalQuantity, Integer remainingQuantity, LocalDateTime startTime,
                             LocalDateTime endTime, GifticonStatus status, boolean canClaim,
                             LocalDateTime createdDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.brand = brand;
        this.totalQuantity = totalQuantity;
        this.remainingQuantity = remainingQuantity;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.canClaim = canClaim;
        this.createdDate = createdDate;
    }

    public static GifticonResponse of(Gifticon gifticon) {
        return GifticonResponse.builder()
                .id(gifticon.getId())
                .title(gifticon.getTitle())
                .description(gifticon.getDescription())
                .imageUrl(gifticon.getImageUrl())
                .brand(gifticon.getBrand())
                .totalQuantity(gifticon.getTotalQuantity())
                .remainingQuantity(gifticon.getRemainingQuantity())
                .startTime(gifticon.getStartTime())
                .endTime(gifticon.getEndTime())
                .status(gifticon.getStatus())
                .canClaim(gifticon.canClaim())
                .createdDate(gifticon.getCreatedDate())
                .build();
    }
} 