package darak.community.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import darak.community.controller.community.event.GifticonController;
import darak.community.domain.Gifticon;
import darak.community.domain.GifticonClaim;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.service.GifticonService;
import darak.community.web.argumentresolver.LoginMemberArgumentResolver;
import darak.community.web.interceptor.LoginCheckInterceptor;
import darak.community.web.session.SessionConst;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = GifticonController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {org.springframework.data.jpa.repository.config.EnableJpaAuditing.class}))
@MockBean(JpaMetamodelMappingContext.class)
@ActiveProfiles("test")
@DisplayName("GifticonController 테스트")
class GifticonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GifticonService gifticonService;

    @MockBean
    private LoginMemberArgumentResolver loginMemberArgumentResolver;

    @MockBean
    private LoginCheckInterceptor loginCheckInterceptor;

    @Autowired
    private ObjectMapper objectMapper;

    private Member testMember;
    private MockHttpSession session;

    @BeforeEach
    void setUp() throws Exception {
        testMember = createTestMember();
        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, testMember);

        // LoginCheckInterceptor Mock 설정 - 기본적으로 통과
        given(loginCheckInterceptor.preHandle(any(), any(), any())).willReturn(true);

        // ArgumentResolver 설정
        given(loginMemberArgumentResolver.supportsParameter(any())).willReturn(true);
        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(testMember);
    }

    @Test
    @DisplayName("기프티콘 목록 조회 - 로그인 상태")
    void gifticonList_Success_LoggedIn() throws Exception {
        // given
        List<Gifticon> gifticons = Arrays.asList(
                createTestGifticon(1L, "스타벅스 아메리카노", "STARBUCKS"),
                createTestGifticon(2L, "맥도날드 빅맥세트", "MCDONALDS")
        );
        List<GifticonClaim> claims = Arrays.asList();

        given(gifticonService.getActiveGifticons()).willReturn(gifticons);
        given(gifticonService.getMemberClaims(testMember)).willReturn(claims);

        // when & then
        mockMvc.perform(get("/community/gifticons")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("gifticon/list"))
                .andExpect(model().attribute("gifticons", hasSize(2)))
                .andExpect(model().attribute("member", testMember))
                .andExpect(model().attributeExists("myClaims"));
    }

    @Test
    @DisplayName("기프티콘 목록 조회 - 비로그인 상태 (인터셉터에 의한 리다이렉트)")
    void gifticonList_Redirect_NotLoggedIn() throws Exception {
        // given
        // 인터셉터가 비로그인 상태를 감지하여 리다이렉트
        given(loginCheckInterceptor.preHandle(any(), any(), any())).willReturn(false);

        // when & then
        mockMvc.perform(get("/community/gifticons"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("기프티콘 신청 - 정상")
    void claimGifticon_Success() throws Exception {
        // given
        Long gifticonId = 1L;
        GifticonClaim claim = GifticonClaim.builder()
                .gifticon(createTestGifticon(gifticonId, "스타벅스 아메리카노", "STARBUCKS"))
                .member(testMember)
                .gifticonCode("TEST1234567890AB")
                .build();

        given(gifticonService.claimGifticon(gifticonId, testMember)).willReturn(claim);

        // when & then
        mockMvc.perform(post("/community/gifticons/{id}/claim", gifticonId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gifticonCode").value("TEST1234567890AB"))
                .andExpect(jsonPath("$.status").value("CLAIMED"));
    }

    @Test
    @DisplayName("기프티콘 신청 실패 - 이미 신청한 기프티콘")
    void claimGifticon_Fail_AlreadyClaimed() throws Exception {
        // given
        Long gifticonId = 1L;
        doThrow(new IllegalStateException("이미 신청한 기프티콘입니다."))
                .when(gifticonService).claimGifticon(gifticonId, testMember);

        // when & then
        mockMvc.perform(post("/community/gifticons/{id}/claim", gifticonId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("이미 신청한 기프티콘입니다."));
    }

    @Test
    @DisplayName("내 기프티콘 페이지 조회 - 정상")
    void myGifticons_Success() throws Exception {
        // given
        List<GifticonClaim> claims = Arrays.asList(
                GifticonClaim.builder()
                        .gifticon(createTestGifticon(1L, "스타벅스 아메리카노", "STARBUCKS"))
                        .member(testMember)
                        .gifticonCode("TEST1234567890AB")
                        .build()
        );
        given(gifticonService.getMemberClaims(testMember)).willReturn(claims);

        // when & then
        mockMvc.perform(get("/community/gifticons/my")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("gifticon/my"))
                .andExpect(model().attribute("myClaims", hasSize(1)))
                .andExpect(model().attribute("member", testMember));
    }

    @Test
    @DisplayName("내 기프티콘 페이지 조회 실패 - 비로그인")
    void myGifticons_Fail_NotLoggedIn() throws Exception {
        // given
        // 세션 없이 요청하고 인터셉터가 리다이렉트하도록 설정
        given(loginCheckInterceptor.preHandle(any(), any(), any())).willReturn(false);

        // when & then
        mockMvc.perform(get("/community/gifticons/my"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?redirectURL=/community/gifticons/my"));
    }

    @Test
    @DisplayName("기프티콘 사용 - 정상")
    void useGifticon_Success() throws Exception {
        // given
        String gifticonCode = "TEST1234567890AB";

        // when & then
        mockMvc.perform(post("/community/gifticons/use")
                        .session(session)
                        .param("gifticonCode", gifticonCode)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("기프티콘이 사용되었습니다."));
    }

    @Test
    @DisplayName("기프티콘 사용 실패 - 존재하지 않는 코드")
    void useGifticon_Fail_InvalidCode() throws Exception {
        // given
        String gifticonCode = "INVALID_CODE";
        doThrow(new IllegalArgumentException("기프티콘을 찾을 수 없습니다."))
                .when(gifticonService).useGifticon(gifticonCode, testMember);

        // when & then
        mockMvc.perform(post("/community/gifticons/use")
                        .session(session)
                        .param("gifticonCode", gifticonCode)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("기프티콘을 찾을 수 없습니다."));
    }

    private Member createTestMember() {
        return Member.builder()
                .loginId("testuser")
                .password("password123!")
                .name("테스트유저")
                .email("test@example.com")
                .birth(LocalDate.of(1990, 1, 1))
                .phone("010-1234-5678")
                .grade(MemberGrade.MEMBER)
                .build();
    }

    private Gifticon createTestGifticon(Long id, String title, String brand) {
        Gifticon gifticon = Gifticon.builder()
                .title(title)
                .description(title + " 설명")
                .brand(brand)
                .totalQuantity(100)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(30))
                .build();

        // 리플렉션을 사용하여 ID 설정 (테스트용)
        try {
            java.lang.reflect.Field idField = Gifticon.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(gifticon, id);
        } catch (Exception e) {
            // 테스트에서는 무시
        }

        return gifticon;
    }
} 