package darak.community.service.event.gifticon;

import darak.community.service.event.gifticon.request.GifticonCreateServiceRequest;
import darak.community.service.event.gifticon.response.GifticonClaimResponse;
import darak.community.service.event.gifticon.response.GifticonResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GifticonService {

    Long createGifticon(GifticonCreateServiceRequest request);

    void activateGifticon(Long gifticonId);

    void deactivateGifticon(Long gifticonId);

    List<GifticonResponse> findActiveGifticonsAll();

    List<GifticonResponse> findActiveGifticonsForMember(Long memberId);

    List<GifticonResponse> findActiveGifticonsLimit(int limit);

    Page<GifticonResponse> getAllGifticons(Pageable pageable);

    GifticonResponse getGifticon(Long gifticonId);

    GifticonClaimResponse claimGifticon(Long gifticonId, Long memberId);

    List<GifticonClaimResponse> getMemberClaims(Long memberId);

    void useGifticon(String gifticonCode, Long memberId);
} 