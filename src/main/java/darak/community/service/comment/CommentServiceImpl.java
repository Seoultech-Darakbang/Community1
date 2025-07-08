package darak.community.service.comment;

import darak.community.domain.comment.Comment;
import darak.community.domain.heart.CommentHeart;
import darak.community.domain.log.DeleteLog;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.domain.post.Post;
import darak.community.infra.repository.CommentHeartRepository;
import darak.community.infra.repository.CommentRepository;
import darak.community.infra.repository.DeleteLogRepository;
import darak.community.infra.repository.MemberRepository;
import darak.community.infra.repository.PostRepository;
import darak.community.service.comment.request.CommentCreateServiceRequest;
import darak.community.service.comment.request.ReplyCreateServiceRequest;
import darak.community.service.comment.response.CommentResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final DeleteLogRepository deleteLogRepository;
    private final CommentHeartRepository commentHeartRepository;

    @Override
    @Transactional
    public void createFromPost(CommentCreateServiceRequest request) {
        Comment comment = Comment.createComment(
                request.getContent(),
                request.isAnonymous(),
                findPostBy(request.getPostId()),
                findMemberBy(request.getMemberId()));

        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void createReplyFromPost(ReplyCreateServiceRequest request) {
        Comment reply = Comment.createReply(
                request.getContent(),
                request.isAnonymous(),
                findPostBy(request.getPostId()),
                findMemberBy(request.getMemberId()),
                findParentCommentBy(request.getParentCommentId())
        );

        commentRepository.save(reply);
    }

    @Override
    @Transactional
    public void deleteCommentBy(Long memberId, Long commentId) {
        validateDeletePermissionBy(memberId, commentId);
        Comment comment = findParentCommentBy(commentId);
        commentRepository.delete(comment);
        deleteLogRepository.save(DeleteLog.commentDeleteLog(comment, findMemberBy(memberId)));
    }

    @Override
    public Page<CommentResponse> findCommentsWithReplyBy(Long memberId, Long postId, Pageable pageable) {
        Page<Comment> parentCommentsPage = commentRepository.findParentCommentsByPostIdPaged(postId, pageable);

        Map<Long, CommentResponse> parentResponseMap = createParentMapBy(parentCommentsPage);
        List<Long> parentCommentIds = getParentCommentIds(parentResponseMap);

        List<Comment> childComments = commentRepository.findChildCommentsByParentIds(parentCommentIds);
        addChildCommentsToParentMap(childComments, parentResponseMap);

        List<CommentResponse> commentResponses = new ArrayList<>(parentResponseMap.values());
        return new PageImpl<>(commentResponses, pageable, parentCommentsPage.getTotalElements());
    }

    @Override
    public Page<CommentResponse> findCommentsBy(Long memberId, Pageable pageable) {
        Page<Comment> myCommentsPage = commentRepository.findByMemberIdPaged(memberId, pageable);
        List<CommentResponse> commentResponses = myCommentsPage.stream()
                .map(CommentResponse::createRootResponse)
                .toList();

        return new PageImpl<>(commentResponses, pageable, myCommentsPage.getTotalElements());
    }

    @Override
    public Page<CommentResponse> findHeartCommentsBy(Long memberId, Pageable pageable) {
        List<CommentHeart> commentHearts = commentHeartRepository.findByMemberIdFetchComments(memberId);
        List<CommentResponse> commentResponses = commentHearts.stream()
                .map(CommentHeart::getComment)
                .map(CommentResponse::createRootResponse)
                .toList();
        return new PageImpl<>(commentResponses, pageable, commentHearts.size());
    }

    private Post findPostBy(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
    }

    private Member findMemberBy(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
    }

    private Comment findParentCommentBy(Long parentCommentId) {
        return commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
    }

    private void validateDeletePermissionBy(Long deleteMemberId, Long commentId) {
        Member member = findMemberBy(deleteMemberId);
        Comment comment = findParentCommentBy(commentId);

        if (cannotHasPermission(comment, member)) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }
    }

    private boolean cannotHasPermission(Comment comment, Member member) {
        return !(comment.getMember().getId().equals(member.getId()) || member.isAtLeastThan(MemberGrade.ADMIN));
    }

    private Map<Long, CommentResponse> createParentMapBy(
            Page<Comment> parentCommentsPage) {
        return parentCommentsPage.stream()
                .map(CommentResponse::createRootResponse)
                .collect(Collectors.toMap(CommentResponse::getId, commentResponse -> commentResponse));
    }

    private List<Long> getParentCommentIds(Map<Long, CommentResponse> commentResponseMap) {
        return commentResponseMap.keySet().stream().toList();
    }

    private void addChildCommentsToParentMap(List<Comment> childComments,
                                             Map<Long, CommentResponse> parentResponseMap) {
        childComments.forEach(comment -> {
            CommentResponse childResponse = CommentResponse.createRootResponse(comment);
            parentResponseMap.get(comment.getParent().getId()).addChild(childResponse);
        });
    }
}
