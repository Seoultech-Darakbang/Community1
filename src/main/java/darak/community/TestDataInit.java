package darak.community;

import darak.community.domain.member.Member;
import darak.community.service.MemberService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataInit implements ApplicationRunner {

    private final MemberService memberService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Member testMember = Member.builder()
                .loginId("test")
                .name("테스트")
                .password("test!")
                .phone("01012345678")
                .birth(LocalDate.of(2000, 1, 1))
                .email("test@test.com")
                .build();

        memberService.join(testMember);
    }
} 