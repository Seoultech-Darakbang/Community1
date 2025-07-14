package darak.community.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.infra.repository.MemberRepository;
import darak.community.service.login.LoginServiceImpl;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService 테스트")
class LoginServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private LoginServiceImpl loginService;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .loginId("testuser")
                .name("테스트 사용자")
                .password("password123")
                .phone("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .email("test@example.com")
                .grade(MemberGrade.MEMBER)
                .build();
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() throws Exception {
        // given
        given(memberRepository.findByLoginId("testuser")).willReturn(Optional.of(testMember));

        // when
        Member result = loginService.login("testuser", "password123");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getLoginId()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_Fail_UserNotFound() throws Exception {
        // given
        given(memberRepository.findByLoginId("nonexistent")).willReturn(Optional.empty());

        // when
        Member result = loginService.login("nonexistent", "password123");

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_Fail_WrongPassword() throws Exception {
        // given
        given(memberRepository.findByLoginId("testuser")).willReturn(Optional.of(testMember));

        // when
        Member result = loginService.login("testuser", "wrongpassword");

        // then
        assertThat(result).isNull();
    }
} 