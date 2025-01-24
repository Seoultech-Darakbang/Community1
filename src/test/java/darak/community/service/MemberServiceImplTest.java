package darak.community.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import darak.community.domain.LoginResult;
import darak.community.domain.member.Member;
import darak.community.dto.MemberUpdateDTO;
import darak.community.repository.MemberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class MemberServiceImplTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberServiceImpl memberServiceImpl;

    @Test
    void 회원가입() throws Exception {
        // given
        Member member = getMember();
        // when
        Long savedId = memberServiceImpl.join(member);
        // then
        assertEquals(member, memberRepository.findById(savedId).get());
        // 생성일 검사
        assertEquals(LocalDate.now().getDayOfMonth(),
                memberRepository.findById(savedId).get().getCreatedDate().getDayOfMonth());
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

        memberServiceImpl.join(member1);
        // when
        assertThrows(IllegalStateException.class, () -> memberServiceImpl.join(member2));
    }

    @Test
    void 회원_정보_수정() throws Exception {
        // given
        Member member1 = Member.builder().name("member1").phone("01012345678").build();
        memberServiceImpl.join(member1);
        // when
        MemberUpdateDTO memberUpdateDTO = new MemberUpdateDTO();
        memberUpdateDTO.setId(member1.getId());
        memberUpdateDTO.setName(member1.getName());
        memberUpdateDTO.setPhone("01098765432");

        memberServiceImpl.update(memberUpdateDTO);
        // then
        assertTrue(memberRepository.findById(member1.getId()).get().isMatchedPhone(memberUpdateDTO.getPhone()));

        assertEquals(LocalDateTime.now().getDayOfMonth(), member1.getLastModifiedDate().getDayOfMonth());
    }

    @Test
    void 회원_정보_수정_중복() throws Exception {
        // given
        Member member1 = Member.builder().name("member1").phone("12345678").build();
        Member member2 = Member.builder().name("member2").phone("11111111").build();
        memberServiceImpl.join(member1);
        memberServiceImpl.join(member2);

        MemberUpdateDTO memberUpdateDTO = new MemberUpdateDTO();
        memberUpdateDTO.setId(member1.getId());
        memberUpdateDTO.setName("member2");
        // when
        assertThrows(IllegalStateException.class, () -> memberServiceImpl.update(memberUpdateDTO));
        // then
    }

    @Test
    void 회원_삭제() throws Exception {
        // given
        Member member = getMember();
        memberServiceImpl.join(member);
        // when
        memberServiceImpl.remove(member.getId());
        // then
        assertTrue(memberRepository.findById(member.getId()).isEmpty());
    }

    @Test
    void 로그인_존재하지않는_회원() throws Exception {
        // given
        Member member = Member.builder().name("admin").password("qwe123").build();
        memberServiceImpl.join(member);
        // when
        assertEquals(memberServiceImpl.login("admin2", "asdf"), LoginResult.NON_EXIST);
    }

    @Test
    void 로그인_실패() throws Exception {
        // given
        Member member = Member.builder().name("admin").password("qwe123").build();
        memberServiceImpl.join(member);
        // when
        assertEquals(memberServiceImpl.login("admin", "asdf"), LoginResult.FAILED);
    }

    @Test
    void 로그인_성공() throws Exception {
        // given
        Member member = Member.builder().name("admin").password("qwe123").build();
        memberServiceImpl.join(member);
        // when
        assertEquals(memberServiceImpl.login("admin", "qwe123"), LoginResult.SUCCESS);

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
        memberServiceImpl.join(member1);

        Member member2 = Member.builder()
                .name("admin2")
                .password("qwe123")
                .birth(LocalDate.of(2001, 10, 13))
                .phone("12345678")
                .build();
        memberServiceImpl.join(member2);

        // when
        List<String> nameList = memberServiceImpl.findMemberNames(LocalDate.of(2001, 10, 13), "12345678");

        // then
        assertEquals(2, nameList.size());
        assertThrows(NoSuchElementException.class,
                () -> memberServiceImpl.findMemberNames(LocalDate.of(2001, 10, 13), "123"));
        assertThrows(NoSuchElementException.class,
                () -> memberServiceImpl.findMemberNames(LocalDate.of(2001, 10, 14), "12345678"));
    }
}