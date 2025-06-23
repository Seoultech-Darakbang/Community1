package darak.community.repository;

import darak.community.domain.gifticon.Gifticon;
import darak.community.domain.gifticon.GifticonClaim;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("GifticonClaimRepository 테스트")
class GifticonClaimRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private GifticonClaimRepository gifticonClaimRepository;

    @Test
    @DisplayName("기프티콘 수령 저장 및 조회")
    void saveAndFind() {
        // given
        Member member = createAndSaveMember("testuser");
        Gifticon gifticon = createAndSaveGifticon("테스트 기프티콘");

        GifticonClaim claim = GifticonClaim.builder()
                .gifticon(gifticon)
                .member(member)
                .gifticonCode("TEST1234567890AB")
                .build();

        // when
        GifticonClaim saved = gifticonClaimRepository.save(claim);
        em.flush();
        em.clear();

        // then
        Optional<GifticonClaim> found = gifticonClaimRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getGifticonCode()).isEqualTo("TEST1234567890AB");
    }

    @Test
    @DisplayName("회원의 기프티콘 수령 여부 확인")
    void existsByGifticonAndMember() {
        // given
        Member member = createAndSaveMember("testuser");
        Gifticon gifticon = createAndSaveGifticon("테스트 기프티콘");

        GifticonClaim claim = GifticonClaim.builder()
                .gifticon(gifticon)
                .member(member)
                .gifticonCode("TEST1234567890AB")
                .build();
        gifticonClaimRepository.save(claim);
        em.flush();
        em.clear();

        // when
        boolean exists = gifticonClaimRepository.existsByGifticonAndMember(gifticon, member);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("회원의 기프티콘 수령 여부 확인 - 수령하지 않은 경우")
    void existsByGifticonAndMember_NotExists() {
        // given
        Member member = createAndSaveMember("testuser");
        Gifticon gifticon = createAndSaveGifticon("테스트 기프티콘");

        // when
        boolean exists = gifticonClaimRepository.existsByGifticonAndMember(gifticon, member);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("회원의 기프티콘 수령 내역 조회 - 최신순")
    void findByMemberOrderByCreatedDateDesc() throws InterruptedException {
        // given
        Member member = createAndSaveMember("testuser");
        Gifticon gifticon1 = createAndSaveGifticon("기프티콘 1");
        Gifticon gifticon2 = createAndSaveGifticon("기프티콘 2");

        GifticonClaim claim1 = GifticonClaim.builder()
                .gifticon(gifticon1)
                .member(member)
                .gifticonCode("CODE1")
                .build();

        GifticonClaim claim2 = GifticonClaim.builder()
                .gifticon(gifticon2)
                .member(member)
                .gifticonCode("CODE2")
                .build();

        gifticonClaimRepository.save(claim1);
        Thread.sleep(10); // 시간 차이를 위해
        gifticonClaimRepository.save(claim2);
        em.flush();
        em.clear();

        // when
        List<GifticonClaim> claims = gifticonClaimRepository.findByMemberOrderByCreatedDateDesc(member);

        // then
        assertThat(claims).hasSize(2);
        assertThat(claims.get(0).getGifticonCode()).isEqualTo("CODE2"); // 최신순
        assertThat(claims.get(1).getGifticonCode()).isEqualTo("CODE1");
    }

    @Test
    @DisplayName("기프티콘 코드와 회원으로 수령 내역 조회")
    void findByGifticonCodeAndMember() {
        // given
        Member member = createAndSaveMember("testuser");
        Gifticon gifticon = createAndSaveGifticon("테스트 기프티콘");
        String gifticonCode = "TEST1234567890AB";

        GifticonClaim claim = GifticonClaim.builder()
                .gifticon(gifticon)
                .member(member)
                .gifticonCode(gifticonCode)
                .build();
        gifticonClaimRepository.save(claim);
        em.flush();
        em.clear();

        // when
        Optional<GifticonClaim> found = gifticonClaimRepository.findByGifticonCodeAndMember(gifticonCode, member);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getGifticonCode()).isEqualTo(gifticonCode);
        assertThat(found.get().getMember()).isEqualTo(member);
    }

    @Test
    @DisplayName("기프티콘별 수령 수량 조회")
    void countByGifticon() {
        // given
        Member member1 = createAndSaveMember("user1");
        Member member2 = createAndSaveMember("user2");
        Gifticon gifticon = createAndSaveGifticon("테스트 기프티콘");

        GifticonClaim claim1 = GifticonClaim.builder()
                .gifticon(gifticon)
                .member(member1)
                .gifticonCode("CODE1")
                .build();

        GifticonClaim claim2 = GifticonClaim.builder()
                .gifticon(gifticon)
                .member(member2)
                .gifticonCode("CODE2")
                .build();

        gifticonClaimRepository.saveAll(List.of(claim1, claim2));
        em.flush();
        em.clear();

        // when
        int count = gifticonClaimRepository.countByGifticon(gifticon);

        // then
        assertThat(count).isEqualTo(2);
    }

    private Member createAndSaveMember(String loginId) {
        Member member = Member.builder()
                .loginId(loginId)
                .name("테스트 사용자 " + loginId)
                .password("password123")
                .phone("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .email(loginId + "@example.com")
                .grade(MemberGrade.MEMBER)
                .build();
        return em.persistAndFlush(member);
    }

    private Gifticon createAndSaveGifticon(String title) {
        Gifticon gifticon = Gifticon.builder()
                .title(title)
                .description("테스트 설명")
                .brand("테스트 브랜드")
                .totalQuantity(100)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(7))
                .build();
        return em.persistAndFlush(gifticon);
    }
} 