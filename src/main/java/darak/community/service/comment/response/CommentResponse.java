package darak.community.service.comment.response;

import darak.community.domain.comment.Comment;
import darak.community.service.member.response.MemberResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentResponse {

    private Long id;

    private String content;

    private boolean anonymous;

    private boolean isMine;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private MemberResponse author;

    private MyCommentHeartResponse commentHeartResponse;

    private List<CommentResponse> children = new ArrayList<>();

    @Builder
    private CommentResponse(Long id, String content, boolean anonymous, boolean isMine, LocalDateTime createdAt,
                            LocalDateTime modifiedAt, MemberResponse author,
                            MyCommentHeartResponse commentHeartResponse,
                            List<CommentResponse> children) {
        this.id = id;
        this.content = content;
        this.anonymous = anonymous;
        this.isMine = isMine;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.author = author;
        this.commentHeartResponse = commentHeartResponse;
    }

    public static CommentResponse createRootResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .anonymous(comment.isAnonymous())
                .isMine(comment.getMember() != null)
                .createdAt(comment.getCreatedDate())
                .modifiedAt(comment.getLastModifiedDate())
                .author(MemberResponse.of(comment.getMember()))
                .commentHeartResponse(MyCommentHeartResponse.of(comment.getMember(), comment))
                .build();
    }

    public void addChild(CommentResponse child) {
        this.children.add(child);
    }
}
