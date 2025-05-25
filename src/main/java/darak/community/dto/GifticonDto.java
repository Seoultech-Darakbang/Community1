package darak.community.dto;

import darak.community.domain.ClaimStatus;
import darak.community.domain.Gifticon;
import darak.community.domain.GifticonClaim;
import darak.community.domain.GifticonStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class GifticonDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        private String title;
        private String description;
        private String imageUrl;
        private String brand;
        private Integer totalQuantity;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
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

        public static Response from(Gifticon gifticon) {
            return Response.builder()
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

        public static List<Response> from(List<Gifticon> gifticons) {
            return gifticons.stream()
                    .map(Response::from)
                    .collect(Collectors.toList());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClaimResponse {
        private Long id;
        private String gifticonCode;
        private ClaimStatus status;
        private LocalDateTime claimTime;
        private LocalDateTime usedTime;
        private boolean isUsed;
        private Response gifticon;

        public static ClaimResponse from(GifticonClaim claim) {
            return ClaimResponse.builder()
                    .id(claim.getId())
                    .gifticonCode(claim.getGifticonCode())
                    .status(claim.getStatus())
                    .claimTime(claim.getCreatedDate())
                    .usedTime(claim.getStatus() == ClaimStatus.USED ? claim.getLastModifiedDate() : null)
                    .isUsed(claim.getStatus() == ClaimStatus.USED)
                    .gifticon(Response.from(claim.getGifticon()))
                    .build();
        }

        public static List<ClaimResponse> from(List<GifticonClaim> claims) {
            return claims.stream()
                    .map(ClaimResponse::from)
                    .collect(Collectors.toList());
        }
    }
} 