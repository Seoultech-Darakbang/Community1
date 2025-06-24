package darak.community.service.event.gifticon;

import darak.community.domain.gifticon.Gifticon;
import darak.community.domain.gifticon.GifticonClaim;
import darak.community.domain.member.Member;
import darak.community.dto.GifticonDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GifticonService {

    // 관리자용 기프티콘 생성
    Long createGifticon(GifticonDto.CreateRequest request);

    // 기프티콘 활성화
    void activateGifticon(Long gifticonId);

    // 기프티콘 비활성화
    void deactivateGifticon(Long gifticonId);

    // 활성화된 기프티콘 목록 조회
    List<Gifticon> getActiveGifticons();

    // 모든 기프티콘 목록 조회 (관리자용)
    Page<Gifticon> getAllGifticons(Pageable pageable);

    // 기프티콘 상세 조회
    Gifticon getGifticon(Long gifticonId);

    // 기프티콘 수령 (선착순)
    GifticonClaim claimGifticon(Long gifticonId, Member member);

    // 회원의 기프티콘 수령 내역 조회
    List<GifticonClaim> getMemberClaims(Member member);

    // 기프티콘 사용
    void useGifticon(String gifticonCode, Member member);
} 