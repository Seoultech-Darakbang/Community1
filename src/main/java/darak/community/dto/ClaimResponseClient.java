package darak.community.dto;

import darak.community.domain.gifticon.ClaimStatus;
import darak.community.domain.gifticon.GifticonClaim;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimResponseClient {

    private Long id;
    private String gifticonCode;
    private ClaimStatus status;
    private LocalDateTime claimTime;
    private LocalDateTime usedTime;
    private boolean isUsed;
    private GifticonResponseClient gifticon;

    public static ClaimResponseClient from(GifticonClaim claim) {
        return ClaimResponseClient.builder()
                .id(claim.getId())
                .gifticonCode(claim.getGifticonCode())
                .status(claim.getStatus())
                .claimTime(claim.getCreatedDate())
                .usedTime(claim.getStatus() == ClaimStatus.USED ? claim.getLastModifiedDate() : null)
                .isUsed(claim.getStatus() == ClaimStatus.USED)
                .gifticon(GifticonResponseClient.from(claim.getGifticon()))
                .build();
    }

    public static List<ClaimResponseClient> from(List<GifticonClaim> claims) {
        return claims.stream()
                .map(ClaimResponseClient::from)
                .collect(Collectors.toList());
    }
}
