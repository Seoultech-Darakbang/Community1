package darak.community.service.event.gifticon.request;

import darak.community.domain.gifticon.Gifticon;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GifticonCreateServiceRequest {

    private String title;
    private String description;
    private String imageUrl;
    private String brand;
    private Integer totalQuantity;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Builder
    private GifticonCreateServiceRequest(String title, String description, String imageUrl, String brand,
                                         Integer totalQuantity, LocalDateTime startTime, LocalDateTime endTime) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl == null || imageUrl.trim().isEmpty() ? null : imageUrl;
        this.brand = brand;
        this.totalQuantity = totalQuantity;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Gifticon toEntity() {
        return Gifticon.builder()
                .title(title)
                .description(description)
                .imageUrl(imageUrl == null || imageUrl.trim().isEmpty() ? null : imageUrl)
                .brand(brand)
                .totalQuantity(totalQuantity)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
} 