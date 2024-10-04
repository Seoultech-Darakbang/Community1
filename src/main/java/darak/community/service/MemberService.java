package darak.community.service;

import darak.community.domain.LoginStatus;
import darak.community.domain.Member;
import darak.community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    // 회원 생성
    @Transactional
    public Long join(Member member) {
        // 중복 회원 검사
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    // 중복 회원 검사 메서드
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다");
        }
    }

    // 단건 조회 (위임)
    public Member findOne(Long id) {
        return memberRepository.findOne(id);
    }

    // 이름으로 검색 (위임)
    public List<Member> findByName(String name) {
        return memberRepository.findByName(name);
    }

    // 회원 정보 수정
    public void updateMember(Member member, Member modifiedMember) {
        if (!member.getName().equals(modifiedMember.getName())) {
            validateDuplicateMember(modifiedMember);
        }
        member.updateMember(modifiedMember);
        memberRepository.save(member);
    }

    // 회원 삭제
    public void removeMember(Member member) {
        memberRepository.withdraw(member);
    }

    public LoginStatus login(String name, String password) {
        List<Member> memberList = memberRepository.findByName(name);
        if (memberList.isEmpty()) return LoginStatus.NONEXIST;

        if (memberList.getFirst().getPassword().equals(password)) {
            return LoginStatus.SUCCESS;
        } else {
            return LoginStatus.FAILED;
        }
    }

    public List<String> findMemberName(LocalDate birthDay, String phoneNumber) {
        List<Member> members = memberRepository.findByBirthAndPhone(birthDay, phoneNumber);
        if (members.isEmpty()) {
            // 필요에 따라 예외를 던지거나 빈 리스트를 반환
            throw new NoSuchElementException("해당 생년월일과 휴대폰 번호를 가진 회원이 없습니다.");
        }
        return members.stream()
                .map(Member::getName)
                .collect(Collectors.toList());
    }



}
