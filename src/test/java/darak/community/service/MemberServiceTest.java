package darak.community.service;

import darak.community.domain.LoginStatus;
import darak.community.domain.Member;
import darak.community.dto.MemberUpdateDTO;
import darak.community.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class MemberServiceTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;
    @Test
    void 회원가입() throws Exception {
        // given
        Member member = getMember();
        // when
        Long savedId = memberService.join(member);
        // then
        assertEquals(member, memberRepository.findOne(savedId));
        // 생성일 검사
        assertEquals(LocalDate.now().getDayOfMonth(), memberRepository.findOne(savedId).getCreatedDate().getDayOfMonth());
    }

    private Member getMember() {
        Member member = Member.builder()
                .name("준서")
                .password("qwe123")
                .email("qwe123@abc.com")
                .phone("01012345678")
                .birth(LocalDate.of(2001, 10, 13))
                .build();
        return member;
    }

    @Test
    void 중복_회원_검증() throws Exception {
        // given
        Member member1 = Member.builder().name("member1").build();
        Member member2 = Member.builder().name("member1").build();

        memberService.join(member1);
        // when
        assertThrows(IllegalStateException.class, () -> memberService.join(member2));
    }

    @Test
    void 회원_정보_수정() throws Exception {
        // given
        Member member1 = Member.builder().name("member1").phone("01012345678").build();
        memberService.join(member1);
        // when
        MemberUpdateDTO memberUpdateDTO = new MemberUpdateDTO();
        memberUpdateDTO.setId(member1.getId());
        memberUpdateDTO.setName(member1.getName());
        memberUpdateDTO.setPhone("01098765432");

        memberService.updateMember(memberUpdateDTO);
        // then
        assertEquals(memberUpdateDTO.getPhone(), memberRepository.findOne(member1.getId()).getPhone());
        assertEquals(LocalDateTime.now().getDayOfMonth(), member1.getLastModifiedDate().getDayOfMonth());
    }

    @Test
    void 회원_정보_수정_중복() throws Exception {
        // given
        Member member1 = Member.builder().name("member1").phone("12345678").build();
        Member member2 = Member.builder().name("member2").phone("11111111").build();
        memberService.join(member1);
        memberService.join(member2);

        MemberUpdateDTO memberUpdateDTO = new MemberUpdateDTO();
        memberUpdateDTO.setId(member1.getId());
        memberUpdateDTO.setName("member2");
        // when
        assertThrows(IllegalStateException.class, () -> memberService.updateMember(memberUpdateDTO));
        // then
    }

    @Test
    void 회원_삭제() throws Exception {
        // given
        Member member = getMember();
        memberService.join(member);
        // when
        memberService.removeMember(member);
        // then
        assertNull(memberRepository.findOne(member.getId()));
    }

    @Test
    void 로그인_존재하지않는_회원() throws Exception {
        // given
        Member member = Member.builder().name("admin").password("qwe123").build();
        memberService.join(member);
        // when
        assertEquals(memberService.login("admin2", "asdf"), LoginStatus.NONEXIST);
    }

    @Test
    void 로그인_실패() throws Exception {
        // given
        Member member = Member.builder().name("admin").password("qwe123").build();
        memberService.join(member);
        // when
        assertEquals(memberService.login("admin", "asdf"), LoginStatus.FAILED);
    }

    @Test
    void 로그인_성공() throws Exception {
        // given
        Member member = Member.builder().name("admin").password("qwe123").build();
        memberService.join(member);
        // when
        assertEquals(memberService.login("admin", "qwe123"), LoginStatus.SUCCESS);

    }

    @Test
    void 회원_이름_찾기() throws Exception {
        // given
        Member member1 = Member.builder()
                .name("admin")
                .password("qwe123")
                .birth(LocalDate.of(2001, 10, 13))
                .phone("12345678")
                .build();
        memberService.join(member1);

        Member member2 = Member.builder()
                .name("admin2")
                .password("qwe123")
                .birth(LocalDate.of(2001, 10, 13))
                .phone("12345678")
                .build();
        memberService.join(member2);

        // when
        List<String> nameList = memberService.findMemberName(LocalDate.of(2001, 10, 13), "12345678");

        // then
        assertEquals(2, nameList.size());
        assertThrows(NoSuchElementException.class, () -> memberService.findMemberName(LocalDate.of(2001, 10, 13), "123"));
        assertThrows(NoSuchElementException.class, () -> memberService.findMemberName(LocalDate.of(2001, 10, 14), "12345678"));
    }
}