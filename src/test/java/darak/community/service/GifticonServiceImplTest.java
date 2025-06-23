package darak.community.service;

import darak.community.domain.gifticon.Gifticon;
import darak.community.domain.gifticon.GifticonClaim;
import darak.community.domain.gifticon.GifticonStatus;
import darak.community.domain.gifticon.ClaimStatus;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.dto.GifticonDto;
import darak.community.repository.GifticonClaimRepository;
import darak.community.repository.GifticonRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GifticonService 테스트")
class GifticonServiceImplTest {

    @Mock
    private GifticonRepository gifticonRepository;

    @Mock
    private GifticonClaimRepository gifticonClaimRepository;

    @InjectMocks
    private GifticonServiceImpl gifticonService;

    @Test
    @DisplayName("기프티콘 생성")
    void createGifticon_Success() {
        // given
        GifticonDto.CreateRequest request = createTestRequest();
        Gifticon gifticon = createTestGifticon();

        given(gifticonRepository.save(any(Gifticon.class))).willReturn(gifticon);

        // when
        Long result = gifticonService.createGifticon(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(gifticon.getId());
        verify(gifticonRepository).save(any(Gifticon.class));
    }

    @Test
    @DisplayName("기프티콘 생성 실패 - totalQuantity가 null")
    void createGifticon_Fail_NullTotalQuantity() {
        // given
        GifticonDto.CreateRequest request = GifticonDto.CreateRequest.builder()
                .title("테스트 기프티콘")
                .description("테스트 설명")
                .brand("테스트 브랜드")
                .totalQuantity(null)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(7))
                .build();

        // when & then
        assertThatThrownBy(() -> gifticonService.createGifticon(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("총 수량은 1 이상이어야 합니다");
    }

    @Test
    @DisplayName("기프티콘 활성화")
    void activateGifticon() {
        // given
        Long gifticonId = 1L;
        Gifticon gifticon = createTestGifticon();
        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.of(gifticon));

        // when
        gifticonService.activateGifticon(gifticonId);

        // then
        assertThat(gifticon.getStatus()).isEqualTo(GifticonStatus.ACTIVE);
    }

    @Test
    @DisplayName("기프티콘 활성화 실패 - 존재하지 않는 기프티콘")
    void activateGifticon_Fail_NotFound() {
        // given
        Long gifticonId = 999L;
        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> gifticonService.activateGifticon(gifticonId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기프티콘을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("기프티콘 비활성화")
    void deactivateGifticon() {
        // given
        Long gifticonId = 1L;
        Gifticon gifticon = createTestGifticon();
        gifticon.activate();
        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.of(gifticon));

        // when
        gifticonService.deactivateGifticon(gifticonId);

        // then
        assertThat(gifticon.getStatus()).isEqualTo(GifticonStatus.CLOSED);
    }

    @Test
    @DisplayName("활성 기프티콘 목록 조회")
    void getActiveGifticons() {
        // given
        List<Gifticon> activeGifticons = Arrays.asList(
                createTestGifticon("기프티콘 1"),
                createTestGifticon("기프티콘 2")
        );
        // findActiveGifticons() 메서드에 매개변수 추가
        given(gifticonRepository.findActiveGifticons(any(GifticonStatus.class), any(LocalDateTime.class)))
                .willReturn(activeGifticons);

        // when
        List<Gifticon> result = gifticonService.getActiveGifticons();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("기프티콘 1");
        assertThat(result.get(1).getTitle()).isEqualTo("기프티콘 2");
    }

    @Test
    @DisplayName("기프티콘 상세 조회")
    void getGifticon() {
        // given
        Long gifticonId = 1L;
        Gifticon gifticon = createTestGifticon();
        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.of(gifticon));

        // when
        Gifticon result = gifticonService.getGifticon(gifticonId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("테스트 기프티콘");
    }

    @Test
    @DisplayName("기프티콘 상세 조회 실패 - 존재하지 않는 기프티콘")
    void getGifticon_Fail_NotFound() {
        // given
        Long gifticonId = 999L;
        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> gifticonService.getGifticon(gifticonId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기프티콘을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("기프티콘 수령 성공")
    void claimGifticon_Success() {
        // given
        Long gifticonId = 1L;
        Member member = createTestMember();
        Gifticon gifticon = createTestGifticon();
        gifticon.activate(); // 활성화

        GifticonClaim claim = createTestClaim(member, "TEST1234567890AB");

        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.of(gifticon));
        given(gifticonClaimRepository.existsByGifticonAndMember(gifticon, member)).willReturn(false);
        given(gifticonClaimRepository.save(any(GifticonClaim.class))).willReturn(claim);

        // when
        GifticonClaim result = gifticonService.claimGifticon(gifticonId, member);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGifticonCode()).isEqualTo("TEST1234567890AB");
        verify(gifticonRepository).findById(gifticonId);
        verify(gifticonClaimRepository).existsByGifticonAndMember(gifticon, member);
        verify(gifticonClaimRepository).save(any(GifticonClaim.class));
    }

    @Test
    @DisplayName("기프티콘 수령 실패 - 수령 불가능한 상태")
    void claimGifticon_Fail_CannotClaim() {
        // given
        Long gifticonId = 1L;
        Member member = createTestMember();
        Gifticon gifticon = createTestGifticon();
        // 기프티콘을 활성화하지 않음 (WAITING 상태)

        given(gifticonRepository.findById(gifticonId)).willReturn(Optional.of(gifticon));
        given(gifticonClaimRepository.existsByGifticonAndMember(gifticon, member)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> gifticonService.claimGifticon(gifticonId, member))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("기프티콘을 받을 수 없는 상태입니다.");  // 실제 메시지로 수정
    }

    @Test
    @DisplayName("회원의 기프티콘 수령 내역 조회")
    void getMemberClaims() {
        // given
        Member member = createTestMember();
        List<GifticonClaim> claims = Arrays.asList(
                createTestClaim(member, "CODE1"),
                createTestClaim(member, "CODE2")
        );

        given(gifticonClaimRepository.findByMemberOrderByCreatedDateDesc(member))
                .willReturn(claims);

        // when
        List<GifticonClaim> result = gifticonService.getMemberClaims(member);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getGifticonCode()).isEqualTo("CODE1");
        assertThat(result.get(1).getGifticonCode()).isEqualTo("CODE2");
    }

    @Test
    @DisplayName("기프티콘 사용 - 정상")
    void useGifticon_Success() {
        // given
        String gifticonCode = "TEST1234567890AB";
        Member member = createTestMember();
        GifticonClaim claim = createTestClaim(member, gifticonCode);

        given(gifticonClaimRepository.findByGifticonCodeAndMember(gifticonCode, member))
                .willReturn(Optional.of(claim));

        // when
        gifticonService.useGifticon(gifticonCode, member);

        // then
        assertThat(claim.getStatus()).isEqualTo(ClaimStatus.USED);
    }

    @Test
    @DisplayName("기프티콘 사용 실패 - 존재하지 않는 기프티콘 코드")
    void useGifticon_Fail_NotFound() {
        // given
        String gifticonCode = "INVALID_CODE";
        Member member = createTestMember();

        given(gifticonClaimRepository.findByGifticonCodeAndMember(gifticonCode, member))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> gifticonService.useGifticon(gifticonCode, member))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기프티콘을 찾을 수 없습니다.");
    }

    private GifticonDto.CreateRequest createTestRequest() {
        return GifticonDto.CreateRequest.builder()
                .title("테스트 기프티콘")
                .description("테스트 설명")
                .brand("테스트 브랜드")
                .totalQuantity(100)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(7))
                .build();
    }

    private Gifticon createTestGifticon() {
        return createTestGifticon("테스트 기프티콘");
    }

    private Gifticon createTestGifticon(String title) {
        Gifticon gifticon = Gifticon.builder()
                .title(title)
                .description("테스트 설명")
                .brand("테스트 브랜드")
                .totalQuantity(100)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(7))
                .build();

        // ID 설정 (테스트용)
        try {
            java.lang.reflect.Field idField = Gifticon.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(gifticon, 1L);
        } catch (Exception e) {
            // ID 설정 실패 시 무시
        }

        return gifticon;
    }

    private Member createTestMember() {
        return Member.builder()
                .loginId("testuser")
                .name("테스트 사용자")
                .password("password123")
                .phone("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .email("test@example.com")
                .grade(MemberGrade.MEMBER) // GENERAL → MEMBER로 변경
                .build();
    }

    private GifticonClaim createTestClaim(Member member, String gifticonCode) {
        return GifticonClaim.builder()
                .gifticon(createTestGifticon())
                .member(member)
                .gifticonCode(gifticonCode)
                .build();
    }
} 