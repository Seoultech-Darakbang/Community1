package darak.community.service;

import darak.community.domain.comment.Comment;
import darak.community.domain.log.DeleteLog;
import darak.community.domain.post.Post;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.repository.CommentRepository;
import darak.community.repository.DeleteLogRepository;
import darak.community.repository.PostRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final DeleteLogRepository deleteLogRepository;

    @Override
    @Transactional
    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void saveFromPost(Member member, Long postId, String content, boolean anonymous) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다"));

        Comment comment = Comment.createComment(content, anonymous, post, member);
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void saveReplyFromPost(Member member, Long postId, Long parentCommentId, String content, boolean anonymous) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다"));

        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다"));

        Comment reply = Comment.createReply(content, anonymous, post, member, parentComment);
        commentRepository.save(reply);
    }

    @Override
    @Transactional
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void deleteWithPermission(Long commentId, Member member) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        if (!hasDeletePermission(comment, member)) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }

        DeleteLog deleteLog = DeleteLog.commentDeleteLog(comment, member);
        deleteLogRepository.save(deleteLog);

        commentRepository.delete(comment);
    }

    private boolean hasDeletePermission(Comment comment, Member member) {
        if (comment.getMember().getId().equals(member.getId())) {
            return true;
        }

        return member.getMemberGrade() == MemberGrade.ADMIN ||
                member.getMemberGrade() == MemberGrade.MASTER;
    }

    @Override
    public List<Comment> findByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    @Override
    public List<Comment> findByMemberId(Long memberId) {
        return commentRepository.findByMemberId(memberId);
    }

    @Override
    public Map<Comment, List<Comment>> findCommentsWithReplies(Long postId) {
        List<Comment> parentComments = commentRepository.findParentCommentsByPostId(postId);

        Map<Comment, List<Comment>> commentsWithReplies = new LinkedHashMap<>();

        for (Comment parentComment : parentComments) {
            List<Comment> replies = commentRepository.findRepliesByParentId(parentComment.getId());
            commentsWithReplies.put(parentComment, replies);
        }

        return commentsWithReplies;
    }

    @Override
    public Map<Comment, List<Comment>> findCommentsWithReplies(Long postId, Pageable pageable) {
        // 페이징된 원댓글들을 먼저 조회
        Page<Comment> parentCommentsPage = commentRepository.findParentCommentsByPostId(postId, pageable);
        List<Comment> parentComments = parentCommentsPage.getContent();

        Map<Comment, List<Comment>> commentsWithReplies = new LinkedHashMap<>();

        for (Comment parentComment : parentComments) {
            List<Comment> replies = commentRepository.findRepliesByParentId(parentComment.getId());
            commentsWithReplies.put(parentComment, replies);
        }

        return commentsWithReplies;
    }

    @Override
    public Page<Comment> findParentCommentsByPostId(Long postId, Pageable pageable) {
        return commentRepository.findParentCommentsByPostId(postId, pageable);
    }
}
