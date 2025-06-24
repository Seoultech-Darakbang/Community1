package darak.community.domain.member;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import darak.community.core.exception.PasswordFailedExceededException;
import org.junit.jupiter.api.Test;

class MemberPasswordTest {
    @Test
    void 비밀번호_일치() throws Exception {
        // given
        MemberPassword memberPassword = MemberPassword.builder().password("qwe123").build();
        // when
        assertTrue(memberPassword.isMatched("qwe123"));
        // then
    }

    @Test
    void 비밀번호_불일치() throws Exception {
        // given
        MemberPassword memberPassword = MemberPassword.builder().password("qwe123").build();
        // when
        assertFalse(memberPassword.isMatched("qwe1234"));
    }

    @Test
    void 비밀번호_실패초과() throws Exception {
        // given
        MemberPassword memberPassword = MemberPassword.builder().password("qwe123").build();
        // when
        for (int i = 0; i < 5; i++) {
            memberPassword.isMatched("qwe1234");
        }
        // then
        assertThrows(PasswordFailedExceededException.class, () -> memberPassword.isMatched("qwe1234"));
    }

    @Test
    void 비밀번호_변경() throws Exception {
        // given
        MemberPassword memberPassword = MemberPassword.builder().password("qwe123").build();
        // when
        memberPassword.changePassword("qwe1234", "qwe123");
        // then
        assertTrue(memberPassword.isMatched("qwe1234"));
    }
}