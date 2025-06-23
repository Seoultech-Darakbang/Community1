package darak.community.dto;

import darak.community.domain.comment.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MyCommentDto {
    private Long id;
    private String content;
    private String postTitle;
    private Long postId;
    private String boardName;
    private Long boardId;
    private LocalDateTime createdDate;
    private int likeCount;
    private boolean isReply;

    public MyCommentDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.postTitle = comment.getPost().getTitle();
        this.postId = comment.getPost().getId();
        this.boardName = comment.getPost().getBoard().getName();
        this.boardId = comment.getPost().getBoard().getId();
        this.createdDate = comment.getCreatedDate();
        this.likeCount = comment.getLikeCount();
        this.isReply = comment.getParent() != null;
    }
} 