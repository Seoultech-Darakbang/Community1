package darak.community.service;

import darak.community.domain.member.Member;
import darak.community.dto.MemberUpdateDTO;
import darak.community.repository.MemberRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    // 회원 생성
    @Transactional
    @Override
    public Long join(Member member) {
        // 중복 회원 검사
        validateDuplicateMember(member.getLoginId());
        memberRepository.save(member);
        return member.getId();
    }

    // 중복 회원 검사 메서드
    public void validateDuplicateMember(String loginId) {
        Optional<Member> findMember = memberRepository.findByLoginId(loginId);
        if (findMember.isPresent()) {
            throw new IllegalStateException("이미 존재하는 회원입니다");
        }
    }

    // 단건 조회 (위임)
    @Override
    public Member findById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    // 이름으로 검색 (위임)
    @Override
    public List<Member> findByName(String name) {
        return memberRepository.findByName(name);
    }

    // 회원 정보 수정
    @Transactional
    @Override
    public void update(MemberUpdateDTO memberUpdateDTO) {
        Member member = memberRepository.findById(memberUpdateDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다.")); // 영속 객체
        if (!memberUpdateDTO.getName().equals(member.getName())) { // 이름을 변경한 경우
            validateDuplicateMember(memberUpdateDTO.getName());
        }
        member.updateMember(memberUpdateDTO.toEntity());
    }

    // 회원 삭제
    @Transactional
    @Override
    public void remove(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        memberRepository.withdraw(member);
    }

    // name 찾기
    @Override
    public List<String> findMemberNames(LocalDate birthDay, String phoneNumber) {
        List<Member> members = memberRepository.findByBirthAndPhone(birthDay, phoneNumber);
        if (members.isEmpty()) {
            throw new NoSuchElementException("해당 생년월일과 휴대폰 번호를 가진 회원이 없습니다.");
        }
        return members.stream()
                .map(Member::getName)
                .collect(Collectors.toList());
    }

}
