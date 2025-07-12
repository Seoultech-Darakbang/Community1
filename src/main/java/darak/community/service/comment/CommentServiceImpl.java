package darak.community.service.comment;

import darak.community.core.auth.ServiceAuth;
import darak.community.domain.comment.Comment;
import darak.community.domain.heart.CommentHeart;
import darak.community.domain.log.AdminLog;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.domain.post.Post;
import darak.community.infra.repository.AdminLogRepository;
import darak.community.infra.repository.CommentHeartRepository;
import darak.community.infra.repository.CommentRepository;
import darak.community.infra.repository.MemberRepository;
import darak.community.infra.repository.PostRepository;
import darak.community.infra.repository.dto.CommentInPostDto;
import darak.community.infra.repository.dto.CommentWithMetaDto;
import darak.community.service.comment.request.CommentCreateServiceRequest;
import darak.community.service.comment.request.CommentDeleteServiceRequest;
import darak.community.service.comment.request.CommentSearch;
import darak.community.service.comment.request.ReplyCreateServiceRequest;
import darak.community.service.comment.response.CommentResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    private final AdminLogRepository adminLogRepository;
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
        Comment comment = findParentCommentBy(commentId);
        Member member = findMemberBy(memberId);
        validateAuthor(member, comment);
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    @ServiceAuth(MemberGrade.ADMIN)
    public void deleteCommentByAdmin(CommentDeleteServiceRequest request) {
        Member member = findMemberBy(request.getMemberId());
        Comment comment = findParentCommentBy(request.getCommentId());

        adminLogRepository.save(AdminLog.commentDeleteLog(comment, member, request.getReason()));
        commentRepository.delete(comment);
    }

    @Override
    public Page<CommentInPostDto> findCommentsInPostBy(Long memberId, Long postId, Pageable pageable) {
        return commentRepository.findCommentInPostByPostIdAndMemberIdPaged(
                postId, memberId, pageable);
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

    @Override
    public Page<CommentWithMetaDto> findCommentsWithMetaBy(Long memberId, Pageable pageable) {
        return commentRepository.findCommentsWithMetaByMemberIdPaged(memberId, pageable);
    }

    @Override
    public Page<CommentWithMetaDto> searchCommentsWithMetaByMemberIdAnd(Long memberId, CommentSearch commentSearch) {
        Pageable pageable = PageRequest.of(commentSearch.getPage(), commentSearch.getSize());
        Page<CommentWithMetaDto> comments = commentRepository.findCommentsWithMetaByMemberIdPaged(
                memberId, pageable);

        if (commentSearch.getBoardName() == null || commentSearch.getBoardName().isEmpty()) {
            return comments;
        }

        List<CommentWithMetaDto> filteredComments = comments.stream()
                .filter(comment -> comment.getBoardName().contains(commentSearch.getBoardName())
                        || comment.getContent().contains(commentSearch.getKeyword()))
                .toList();

        return new PageImpl<>(filteredComments, pageable, filteredComments.size());
    }

    @Override
    public Page<CommentWithMetaDto> findCommentsWithMetaByMemberIdAndHearted(Long memberId, Pageable pageable) {
        return commentRepository.findCommentsWithMetaByMemberLiked(memberId, pageable);
    }

    @Override
    public long getTotalCommentCount() {
        return commentRepository.count();
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

    private void validateAuthor(Member member, Comment comment) {
        if (!isCommentAuthor(comment, member)) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }
    }

    private boolean isCommentAuthor(Comment comment, Member member) {
        return comment.getMember().equals(member);
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
