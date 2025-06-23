package darak.community.service;

import darak.community.domain.gifticon.Gifticon;
import darak.community.domain.gifticon.GifticonClaim;
import darak.community.domain.gifticon.GifticonStatus;
import darak.community.domain.member.Member;
import darak.community.dto.GifticonDto;
import darak.community.repository.GifticonClaimRepository;
import darak.community.repository.GifticonRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GifticonServiceImpl implements GifticonService {

    private final GifticonRepository gifticonRepository;
    private final GifticonClaimRepository gifticonClaimRepository;

    @Override
    public Long createGifticon(GifticonDto.CreateRequest request) {
        log.info("=== GifticonService.createGifticon 호출됨 ===");
        log.info("Request parameters:");
        log.info("  title: {}", request.getTitle());
        log.info("  description: {}", request.getDescription());
        log.info("  brand: {}", request.getBrand());
        log.info("  totalQuantity: {}", request.getTotalQuantity());
        log.info("  startTime: {}", request.getStartTime());
        log.info("  endTime: {}", request.getEndTime());
        log.info("  imageUrl: {}", request.getImageUrl());

        // totalQuantity 검증
        if (request.getTotalQuantity() == null || request.getTotalQuantity() <= 0) {
            log.error("totalQuantity 검증 실패: {}", request.getTotalQuantity());
            throw new IllegalArgumentException("총 수량은 1 이상이어야 합니다. 현재 값: " + request.getTotalQuantity());
        }

        log.info("검증 통과, Gifticon 객체 생성 시작...");

        String imageUrl = (request.getImageUrl() != null && !request.getImageUrl().trim().isEmpty())
                ? request.getImageUrl()
                : null;

        Gifticon gifticon = Gifticon.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(imageUrl)
                .brand(request.getBrand())
                .totalQuantity(request.getTotalQuantity())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        log.info("Gifticon 객체 생성 완료");
        log.info("  - totalQuantity: {}", gifticon.getTotalQuantity());
        log.info("  - remainingQuantity: {}", gifticon.getRemainingQuantity());
        log.info("  - status: {}", gifticon.getStatus());

        log.info("데이터베이스 저장 시작...");
        Gifticon savedGifticon = gifticonRepository.save(gifticon);
        log.info("데이터베이스 저장 완료: ID = {}", savedGifticon.getId());

        return savedGifticon.getId();
    }

    @Override
    public void activateGifticon(Long gifticonId) {
        Gifticon gifticon = getGifticon(gifticonId);
        gifticon.activate();
        log.info("기프티콘 활성화됨: {}", gifticon.getTitle());
    }

    @Override
    public void deactivateGifticon(Long gifticonId) {
        Gifticon gifticon = getGifticon(gifticonId);
        gifticon.close();
        log.info("기프티콘 비활성화됨: {}", gifticon.getTitle());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Gifticon> getActiveGifticons() {
        return gifticonRepository.findActiveGifticons(GifticonStatus.ACTIVE, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Gifticon> getAllGifticons(Pageable pageable) {
        return gifticonRepository.findAllByOrderByCreatedDateDesc(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Gifticon getGifticon(Long gifticonId) {
        return gifticonRepository.findById(gifticonId)
                .orElseThrow(() -> new IllegalArgumentException("기프티콘을 찾을 수 없습니다."));
    }

    @Override
    @Transactional
    public GifticonClaim claimGifticon(Long gifticonId, Member member) {
        Gifticon gifticon = gifticonRepository.findById(gifticonId)
                .orElseThrow(() -> new IllegalArgumentException("기프티콘을 찾을 수 없습니다."));

        if (gifticonClaimRepository.existsByGifticonAndMember(gifticon, member)) {
            throw new IllegalStateException("이미 수령한 기프티콘입니다.");
        }

        // 수령 가능 여부 확인해서 수량 차감
        gifticon.claimOne();

        // 기프티콘 코드 생성
        String gifticonCode = generateGifticonCode();

        GifticonClaim claim = GifticonClaim.builder()
                .gifticon(gifticon)
                .member(member)
                .gifticonCode(gifticonCode)
                .build();

        GifticonClaim savedClaim = gifticonClaimRepository.save(claim);
        log.info("기프티콘 수령됨: {} by {}", gifticon.getTitle(), member.getName());
        return savedClaim;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GifticonClaim> getMemberClaims(Member member) {
        return gifticonClaimRepository.findByMemberOrderByCreatedDateDesc(member);
    }

    @Override
    public void useGifticon(String gifticonCode, Member member) {
        GifticonClaim claim = gifticonClaimRepository.findByGifticonCodeAndMember(gifticonCode, member)
                .orElseThrow(() -> new IllegalArgumentException("기프티콘을 찾을 수 없습니다."));

        claim.use();
        log.info("기프티콘 사용됨: {} by {}", claim.getGifticon().getTitle(), member.getName());
    }

    private String generateGifticonCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
} 