package darak.community.service;

import darak.community.domain.member.Member;
import darak.community.exception.PasswordFailedExceededException;
import darak.community.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Member login(String loginId, String password) throws PasswordFailedExceededException {
        Optional<Member> byLoginId = memberRepository.findByLoginId(loginId);

        if (byLoginId.isEmpty()) {
            return null;
        }

        Member member = byLoginId.get();
        if (member.isMatchedPassword(password)) {
            return member;
        }

        return null;
    }
}
