package darak.community.integration;

import darak.community.domain.Gifticon;
import darak.community.domain.GifticonClaim;
import darak.community.domain.GifticonStatus;
import darak.community.domain.ClaimStatus;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.dto.GifticonDto;
import darak.community.repository.GifticonClaimRepository;
import darak.community.repository.GifticonRepository;
import darak.community.repository.MemberRepository;
import darak.community.service.GifticonService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("기프티콘 통합 테스트")
class GifticonIntegrationTest {

    @Autowired
    private GifticonService gifticonService;

    @Autowired
    private GifticonRepository gifticonRepository;

    @Autowired
    private GifticonClaimRepository gifticonClaimRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 데이터 정리
        cleanupData();
        
        testMember = createAndSaveMember("testuser");
    }

    @AfterEach
    void tearDown() {
        // 각 테스트 후에 데이터 정리
        cleanupData();
    }

    private void cleanupData() {
        // 클레임 먼저 삭제
        List<GifticonClaim> claims = gifticonClaimRepository.findAll();
        for (GifticonClaim claim : claims) {
            gifticonClaimRepository.delete(claim);
        }
        
        // 기프티콘 삭제
        List<Gifticon> gifticons = gifticonRepository.findAll();
        for (Gifticon gifticon : gifticons) {
            gifticonRepository.delete(gifticon);
        }
        
        // 멤버 삭제
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            memberRepository.withdraw(member);
        }
    }

    @Test
    @DisplayName("기프티콘 생성부터 수령까지의 전체 플로우")
    void fullGifticonFlow() {
        // 1. 기프티콘 생성
        GifticonDto.CreateRequest createRequest = GifticonDto.CreateRequest.builder()
                .title("스타벅스 아메리카노")
                .description("따뜻한 아메리카노 1잔")
                .brand("스타벅스")
                .totalQuantity(10)
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        Long gifticonId = gifticonService.createGifticon(createRequest);
        assertThat(gifticonId).isNotNull();

        // 2. 기프티콘 활성화
        gifticonService.activateGifticon(gifticonId);

        // 3. 기프티콘 조회
        Gifticon gifticon = gifticonService.getGifticon(gifticonId);
        assertThat(gifticon.getStatus()).isEqualTo(GifticonStatus.ACTIVE);
        assertThat(gifticon.canClaim()).isTrue();

        // 4. 기프티콘 수령
        GifticonClaim claim = gifticonService.claimGifticon(gifticonId, testMember);
        assertThat(claim).isNotNull();
        assertThat(claim.getGifticonCode()).isNotNull();

        // 5. 수령 후 수량 확인
        Gifticon updatedGifticon = gifticonService.getGifticon(gifticonId);
        assertThat(updatedGifticon.getRemainingQuantity()).isEqualTo(9);

        // 6. 내 기프티콘 목록에서 확인
        List<GifticonClaim> myClaims = gifticonService.getMemberClaims(testMember);
        assertThat(myClaims).hasSize(1);
        assertThat(myClaims.get(0).getGifticonCode()).isEqualTo(claim.getGifticonCode());

        // 7. 기프티콘 사용
        gifticonService.useGifticon(claim.getGifticonCode(), testMember);

        // 8. 사용 후 상태 확인
        List<GifticonClaim> updatedClaims = gifticonService.getMemberClaims(testMember);
        assertThat(updatedClaims.get(0).getStatus()).isEqualTo(ClaimStatus.USED);
    }

    @Test
    @DisplayName("동시성 테스트 - 여러 사용자가 동시에 수령 시도")
    void concurrentClaimTest() throws InterruptedException {
        // given
        GifticonDto.CreateRequest createRequest = GifticonDto.CreateRequest.builder()
                .title("한정 기프티콘")
                .description("한정 수량")
                .brand("테스트 브랜드")
                .totalQuantity(5) // 5개 한정
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        Long gifticonId = gifticonService.createGifticon(createRequest);
        gifticonService.activateGifticon(gifticonId);

        // 10명의 사용자 생성
        int userCount = 10;
        Member[] members = new Member[userCount];
        for (int i = 0; i < userCount; i++) {
            members[i] = createAndSaveMember("user" + i);
        }

        // when - 동시에 수령 시도
        ExecutorService executorService = Executors.newFixedThreadPool(userCount);
        CountDownLatch latch = new CountDownLatch(userCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < userCount; i++) {
            final Member member = members[i];
            executorService.submit(() -> {
                try {
                    gifticonService.claimGifticon(gifticonId, member);
                    successCount.incrementAndGet();
                    System.out.println("성공: " + member.getLoginId());
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.out.println("실패: " + member.getLoginId() + " - " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        System.out.println("성공: " + successCount.get() + ", 실패: " + failCount.get());
        
        assertThat(successCount.get()).isEqualTo(5); // 5명만 성공
        assertThat(failCount.get()).isEqualTo(5); // 5명은 실패

        Gifticon finalGifticon = gifticonService.getGifticon(gifticonId);
        assertThat(finalGifticon.getRemainingQuantity()).isEqualTo(0);
        
        // 실제로 수령된 클레임 수 확인
        List<GifticonClaim> claims = gifticonClaimRepository.findAll();
        assertThat(claims).hasSize(5);
    }

    @Test
    @DisplayName("중복 수령 방지 테스트")
    void preventDuplicateClaim() {
        // given
        GifticonDto.CreateRequest createRequest = GifticonDto.CreateRequest.builder()
                .title("중복 방지 테스트")
                .description("한 사용자가 여러 번 수령 시도")
                .brand("테스트 브랜드")
                .totalQuantity(10)
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        Long gifticonId = gifticonService.createGifticon(createRequest);
        gifticonService.activateGifticon(gifticonId);

        // when - 첫 번째 수령 성공
        GifticonClaim firstClaim = gifticonService.claimGifticon(gifticonId, testMember);
        assertThat(firstClaim).isNotNull();

        // then - 두 번째 수령 시도 실패
        assertThatThrownBy(() -> gifticonService.claimGifticon(gifticonId, testMember))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 수령한 기프티콘입니다.");
    }

    @Test
    @DisplayName("기프티콘 상태 자동 업데이트 테스트")
    void autoStatusUpdate() {
        // given
        GifticonDto.CreateRequest createRequest = GifticonDto.CreateRequest.builder()
                .title("상태 업데이트 테스트")
                .description("수량이 모두 소진되면 품절로 변경")
                .brand("테스트 브랜드")
                .totalQuantity(1) // 1개만 생성
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        Long gifticonId = gifticonService.createGifticon(createRequest);
        gifticonService.activateGifticon(gifticonId);

        // when - 마지막 기프티콘 수령
        gifticonService.claimGifticon(gifticonId, testMember);

        // then - 상태가 품절로 변경됨
        Gifticon gifticon = gifticonService.getGifticon(gifticonId);
        assertThat(gifticon.getRemainingQuantity()).isEqualTo(0);
        assertThat(gifticon.canClaim()).isFalse();
    }

    @Test
    @DisplayName("기프티콘 생성 및 활성화 테스트")
    @Transactional
    void createAndActivateGifticonTest() {
        // given
        GifticonDto.CreateRequest createRequest = GifticonDto.CreateRequest.builder()
                .title("테스트 기프티콘")
                .description("테스트 설명")
                .brand("테스트 브랜드")
                .totalQuantity(10)
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusDays(1))
                .build();

        // when
        Long gifticonId = gifticonService.createGifticon(createRequest);
        
        // then
        Gifticon gifticon = gifticonService.getGifticon(gifticonId);
        assertThat(gifticon.getTitle()).isEqualTo("테스트 기프티콘");
        assertThat(gifticon.getTotalQuantity()).isEqualTo(10);
        assertThat(gifticon.getRemainingQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("기프티콘 수령 테스트")
    @Transactional
    void claimGifticonTest() {
        // given
        GifticonDto.CreateRequest createRequest = GifticonDto.CreateRequest.builder()
                .title("수령 테스트 기프티콘")
                .description("수령 테스트")
                .brand("테스트 브랜드")
                .totalQuantity(3)
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        Long gifticonId = gifticonService.createGifticon(createRequest);
        gifticonService.activateGifticon(gifticonId);

        // when
        GifticonClaim claim = gifticonService.claimGifticon(gifticonId, testMember);

        // then
        assertThat(claim).isNotNull();
        assertThat(claim.getMember()).isEqualTo(testMember);
        assertThat(claim.getGifticonCode()).isNotNull();
        
        Gifticon updatedGifticon = gifticonService.getGifticon(gifticonId);
        assertThat(updatedGifticon.getRemainingQuantity()).isEqualTo(2);
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
        
        memberRepository.save(member);
        memberRepository.flush(); // 즉시 DB에 반영
        return member;
    }
} 