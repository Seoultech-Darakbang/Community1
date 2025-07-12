package darak.community.dto;

import darak.community.domain.gifticon.Gifticon;
import darak.community.domain.gifticon.GifticonStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GifticonResponseClient {

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

    public static GifticonResponseClient from(Gifticon gifticon) {
        return GifticonResponseClient.builder()
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
