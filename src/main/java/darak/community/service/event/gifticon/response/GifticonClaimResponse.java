package darak.community.service.event.gifticon.response;

import darak.community.domain.gifticon.ClaimStatus;
import darak.community.domain.gifticon.GifticonClaim;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GifticonClaimResponse {

    private Long id;
    private String gifticonCode;
    private ClaimStatus status;
    private LocalDateTime claimTime;
    private LocalDateTime usedTime;
    private boolean used;
    private GifticonResponse gifticon;

    @Builder
    private GifticonClaimResponse(Long id, String gifticonCode, ClaimStatus status, LocalDateTime claimTime,
                                   LocalDateTime usedTime, boolean used, GifticonResponse gifticon) {
        this.id = id;
        this.gifticonCode = gifticonCode;
        this.status = status;
        this.claimTime = claimTime;
        this.usedTime = usedTime;
        this.used = used;
        this.gifticon = gifticon;
    }

    public static GifticonClaimResponse of(GifticonClaim claim) {
        return GifticonClaimResponse.builder()
                .id(claim.getId())
                .gifticonCode(claim.getGifticonCode())
                .status(claim.getStatus())
                .claimTime(claim.getCreatedDate())
                .usedTime(claim.getStatus() == ClaimStatus.USED ? claim.getLastModifiedDate() : null)
                .used(claim.getStatus() == ClaimStatus.USED)
                .gifticon(GifticonResponse.of(claim.getGifticon()))
                .build();
    }
} 