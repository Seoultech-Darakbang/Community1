package darak.community.domain;

import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("GifticonClaim 도메인 테스트")
class GifticonClaimTest {

    @Test
    @DisplayName("기프티콘 수령 생성 - 정상")
    void createGifticonClaim_Success() {
        // given
        Gifticon gifticon = createTestGifticon();
        Member member = createTestMember();
        String gifticonCode = "TEST1234567890AB";

        // when
        GifticonClaim claim = GifticonClaim.builder()
                .gifticon(gifticon)
                .member(member)
                .gifticonCode(gifticonCode)
                .build();

        // then
        assertThat(claim.getGifticon()).isEqualTo(gifticon);
        assertThat(claim.getMember()).isEqualTo(member);
        assertThat(claim.getGifticonCode()).isEqualTo(gifticonCode);
        assertThat(claim.getStatus()).isEqualTo(ClaimStatus.CLAIMED);
    }

    @Test
    @DisplayName("기프티콘 사용 - 정상")
    void useGifticon_Success() {
        // given
        GifticonClaim claim = createTestClaim();

        // when
        claim.use();

        // then
        assertThat(claim.getStatus()).isEqualTo(ClaimStatus.USED);
    }

    @Test
    @DisplayName("기프티콘 사용 실패 - 이미 사용됨")
    void useGifticon_Fail_AlreadyUsed() {
        // given
        GifticonClaim claim = createTestClaim();
        claim.use(); // 먼저 사용

        // when & then
        assertThatThrownBy(claim::use)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 사용된 기프티콘입니다.");
    }

    @Test
    @DisplayName("기프티콘 사용 여부 확인")
    void isUsed() {
        // given
        GifticonClaim claim = createTestClaim();

        // when & then
        assertThat(claim.getStatus()).isEqualTo(ClaimStatus.CLAIMED);

        claim.use();
        assertThat(claim.getStatus()).isEqualTo(ClaimStatus.USED);
    }

    private GifticonClaim createTestClaim() {
        return GifticonClaim.builder()
                .gifticon(createTestGifticon())
                .member(createTestMember())
                .gifticonCode("TEST1234567890AB")
                .build();
    }

    private Gifticon createTestGifticon() {
        return Gifticon.builder()
                .title("테스트 기프티콘")
                .description("테스트 설명")
                .brand("테스트 브랜드")
                .totalQuantity(100)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(7))
                .build();
    }

    private Member createTestMember() {
        return Member.builder()
                .loginId("testuser")
                .name("테스트 사용자")
                .password("password123")
                .phone("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .email("test@example.com")
                .grade(MemberGrade.MEMBER)
                .build();
    }
} 