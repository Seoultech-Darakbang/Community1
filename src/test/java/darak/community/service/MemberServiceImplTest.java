package darak.community.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.dto.MemberUpdateDTO;
import darak.community.infra.repository.MemberRepository;
import darak.community.service.member.MemberServiceImpl;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 테스트")
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    @Test
    @DisplayName("회원 가입 성공")
    void join_Success() {
        // given
        Member member = createTestMember();
        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.empty());
        doNothing().when(memberRepository).save(member);

        // when
        Long result = memberService.join(member);

        // then
        assertThat(result).isEqualTo(member.getId());
        verify(memberRepository).findByLoginId(member.getLoginId());
        verify(memberRepository).save(member);
    }

    @Test
    @DisplayName("회원 가입 실패 - 중복된 아이디")
    void join_Fail_DuplicateLoginId() {
        // given
        Member member = createTestMember();
        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> memberService.join(member))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 회원입니다");
    }

    @Test
    @DisplayName("ID로 회원 조회 성공")
    void findById_Success() {
        // given
        Long memberId = 1L;
        Member member = createTestMember();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // when
        Member result = memberService.findById(memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getLoginId()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("ID로 회원 조회 실패 - 존재하지 않는 회원")
    void findById_Fail_NotFound() {
        // given
        Long memberId = 999L;
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.findById(memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("로그인 ID로 회원 조회 성공")
    void findByLoginId_Success() {
        // given
        String loginId = "testuser";
        Member member = createTestMember();
        given(memberRepository.findByLoginId(loginId)).willReturn(Optional.of(member));

        // when
        Member result = memberService.findByLoginId(loginId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getLoginId()).isEqualTo(loginId);
    }

    @Test
    @DisplayName("이름으로 회원 조회")
    void findByName() {
        // given
        String name = "테스트 사용자";
        List<Member> members = Arrays.asList(createTestMember(), createTestMember());
        given(memberRepository.findByName(name)).willReturn(members);

        // when
        List<Member> result = memberService.findByName(name);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("중복 회원 검사 - 중복 없음")
    void validateDuplicateMember_Success() {
        // given
        String loginId = "newuser";
        given(memberRepository.findByLoginId(loginId)).willReturn(Optional.empty());

        // when & then
        assertThatCode(() -> memberService.validateDuplicateMember(loginId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("중복 회원 검사 - 중복 있음")
    void validateDuplicateMember_Fail() {
        // given
        String loginId = "existinguser";
        given(memberRepository.findByLoginId(loginId)).willReturn(Optional.of(createTestMember()));

        // when & then
        assertThatThrownBy(() -> memberService.validateDuplicateMember(loginId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 회원입니다");
    }

    @Test
    @DisplayName("회원 정보 수정")
    void update() {
        // given
        MemberUpdateDTO updateDTO = new MemberUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setName("새로운 이름");
        updateDTO.setPhone("010-9999-9999");
        updateDTO.setEmail("newemail@example.com");

        Member existingMember = createTestMember();
        given(memberRepository.findById(1L)).willReturn(Optional.of(existingMember));
        given(memberRepository.findByLoginId("새로운 이름")).willReturn(Optional.empty());

        // when & then
        assertThatCode(() -> memberService.update(updateDTO))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("회원 삭제")
    void remove() {
        // given
        Long memberId = 1L;
        Member member = createTestMember();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        doNothing().when(memberRepository).withdraw(member);

        // when & then
        assertThatCode(() -> memberService.remove(memberId))
                .doesNotThrowAnyException();
        verify(memberRepository).withdraw(member);
    }

    @Test
    @DisplayName("생년월일과 휴대폰번호로 회원 이름 찾기")
    void findMemberNames() {
        // given
        LocalDate birthDay = LocalDate.of(1990, 1, 1);
        String phoneNumber = "010-1234-5678";
        List<Member> members = Arrays.asList(createTestMember(), createTestMember2());
        given(memberRepository.findByBirthAndPhone(birthDay, phoneNumber)).willReturn(members);

        // when
        List<String> result = memberService.findMemberNames(birthDay, phoneNumber);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).contains("테스트 사용자", "테스트 사용자2");
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

    private Member createTestMember2() {
        return Member.builder()
                .loginId("testuser2")
                .name("테스트 사용자2")
                .password("password123")
                .phone("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .email("test2@example.com")
                .grade(MemberGrade.MEMBER)
                .build();
    }
}