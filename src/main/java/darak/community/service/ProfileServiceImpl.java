package darak.community.service;

import darak.community.domain.comment.Comment;
import darak.community.domain.post.Post;
import darak.community.domain.member.Member;
import darak.community.dto.MyCommentDto;
import darak.community.dto.MyPostDto;
import darak.community.dto.ProfileDto;
import darak.community.infra.repository.CommentHeartRepository;
import darak.community.infra.repository.CommentRepository;
import darak.community.infra.repository.MemberRepository;
import darak.community.infra.repository.PostHeartRepository;
import darak.community.infra.repository.PostRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostHeartRepository postHeartRepository;
    private final CommentHeartRepository commentHeartRepository;
    private final MemberRepository memberRepository;

    @Override
    public ProfileDto getProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        long postCount = postRepository.countByMemberId(memberId);
        long commentCount = commentRepository.countByMemberId(memberId);
        long postLikeCount = postRepository.countLikesByMemberId(memberId);
        long commentLikeCount = commentRepository.countLikesByMemberId(memberId);
        long receivedLikeCount = postLikeCount + commentLikeCount;

        return new ProfileDto(member, postCount, commentCount, receivedLikeCount);
    }

    @Override
    public Page<MyPostDto> getMyPosts(Long memberId, Pageable pageable) {
        Page<Post> posts = postRepository.findByMemberIdPaged(memberId, pageable);
        List<MyPostDto> postDtos = posts.getContent().stream()
                .map(MyPostDto::new)
                .collect(Collectors.toList());

        return new PageImpl<>(postDtos, pageable, posts.getTotalElements());
    }

    @Override
    public Page<MyCommentDto> getMyComments(Long memberId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByMemberIdPaged(memberId, pageable);
        List<MyCommentDto> commentDtos = comments.getContent().stream()
                .map(MyCommentDto::new)
                .collect(Collectors.toList());

        return new PageImpl<>(commentDtos, pageable, comments.getTotalElements());
    }

    @Override
    public Page<MyPostDto> getLikedPosts(Long memberId, Pageable pageable) {
        Page<Post> likedPosts = postHeartRepository.findLikedPostsByMember(memberId, pageable);
        List<MyPostDto> postDtos = likedPosts.getContent().stream()
                .map(MyPostDto::new)
                .collect(Collectors.toList());

        return new PageImpl<>(postDtos, pageable, likedPosts.getTotalElements());
    }

    @Override
    public Page<MyCommentDto> getLikedComments(Long memberId, Pageable pageable) {
        Page<Comment> likedComments = commentHeartRepository.findLikedCommentsByMember(memberId, pageable);
        List<MyCommentDto> commentDtos = likedComments.getContent().stream()
                .map(MyCommentDto::new)
                .collect(Collectors.toList());

        return new PageImpl<>(commentDtos, pageable, likedComments.getTotalElements());
    }

    @Override
    public Page<MyPostDto> searchMyPosts(Long memberId, String keyword, String boardName, Pageable pageable) {
        Page<Post> posts = postRepository.searchMyPosts(memberId, keyword, boardName, pageable);
        List<MyPostDto> postDtos = posts.getContent().stream()
                .map(MyPostDto::new)
                .collect(Collectors.toList());

        return new PageImpl<>(postDtos, pageable, posts.getTotalElements());
    }

    @Override
    public Page<MyCommentDto> searchMyComments(Long memberId, String keyword, String boardName, Pageable pageable) {
        Page<Comment> comments = commentRepository.searchMyComments(memberId, keyword, boardName, pageable);
        List<MyCommentDto> commentDtos = comments.getContent().stream()
                .map(MyCommentDto::new)
                .collect(Collectors.toList());

        return new PageImpl<>(commentDtos, pageable, comments.getTotalElements());
    }

    @Override
    @Transactional
    public void updateProfile(Long memberId, String email, String phone) {
        log.info("=== 프로필 업데이트 시작 ===");
        log.info("memberId: {}, email: {}, phone: {}", memberId, email, phone);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        log.info("=== 업데이트 전 ===");
        log.info("현재 email: [{}]", member.getEmail());
        log.info("현재 phone: [{}]", member.getPhone());

        String beforeEmail = member.getEmail();
        String beforePhone = member.getPhone();

        if (email != null && !email.trim().isEmpty() && !email.equals(beforeEmail)) {
            log.info("이메일 업데이트 수행: [{}] -> [{}]", beforeEmail, email);
            member.updateEmail(email);
        } else {
            log.info("이메일 업데이트 스킵: email={}, beforeEmail={}", email, beforeEmail);
        }

        if (phone != null && !phone.trim().isEmpty() && !phone.equals(beforePhone)) {
            log.info("전화번호 업데이트 수행: [{}] -> [{}]", beforePhone, phone);
            member.updatePhone(phone);
        } else {
            log.info("전화번호 업데이트 스킵: phone={}, beforePhone={}", phone, beforePhone);
        }

        log.info("=== flush 실행 ===");
        memberRepository.saveAndFlush(member);

        log.info("=== 업데이트 후 (메모리) ===");
        log.info("업데이트 후 email: [{}]", member.getEmail());
        log.info("업데이트 후 phone: [{}]", member.getPhone());

        log.info("=== DB 재조회 확인 ===");
        memberRepository.flush(); // 혹시 모를 플러시
        Member reloaded = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        log.info("DB에서 재조회한 email: [{}]", reloaded.getEmail());
        log.info("DB에서 재조회한 phone: [{}]", reloaded.getPhone());

        log.info("=== 프로필 업데이트 완료 ===");
    }
} 