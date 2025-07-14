package darak.community.service.member;

import darak.community.domain.member.Member;
import darak.community.infra.repository.CommentHeartRepository;
import darak.community.infra.repository.CommentRepository;
import darak.community.infra.repository.MemberRepository;
import darak.community.infra.repository.PostHeartRepository;
import darak.community.infra.repository.PostRepository;
import darak.community.service.member.request.ProfileUpdateServiceRequest;
import darak.community.service.member.response.ProfileResponse;
import darak.community.service.member.response.ProfileStatsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ProfileResponse getProfileBy(Long memberId) {
        Member member = findMemberBy(memberId);
        return ProfileResponse.of(member);
    }

    // TODO: 개선 필요
    @Override
    public ProfileStatsResponse getProfileStats(Long memberId) {
        Member member = findMemberBy(memberId);

        long postCount = postRepository.countByMemberId(memberId);
        long commentCount = commentRepository.countByMemberId(memberId);
        long postLikeCount = postRepository.countLikesByMemberId(memberId);
        long commentLikeCount = commentRepository.countLikesByMemberId(memberId);
        long receivedLikeCount = postLikeCount + commentLikeCount;

        return ProfileStatsResponse.builder()
                .postCount(postCount)
                .commentCount(commentCount)
                .postLikeCount(postLikeCount)
                .commentLikeCount(commentLikeCount)
                .receivedLikeCount(receivedLikeCount)
                .build();
    }

    @Override
    @Transactional
    public void updateProfile(Long memberId, ProfileUpdateServiceRequest request) {
        Member member = findMemberBy(memberId);
        member.updateMember(request.getName(), request.getPhone(), request.getBirth(), request.getEmail());
    }

    private Member findMemberBy(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }
}