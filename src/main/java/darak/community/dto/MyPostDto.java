package darak.community.dto;

import darak.community.domain.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MyPostDto {
    private Long id;
    private String title;
    private String boardName;
    private Long boardId;
    private LocalDateTime createdDate;
    private Long viewCount;
    private int likeCount;
    private int commentCount;

    public MyPostDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.boardName = post.getBoard().getName();
        this.boardId = post.getBoard().getId();
        this.createdDate = post.getCreatedDate();
        this.viewCount = post.getReadCount();
        this.likeCount = post.getLikeCount();
        this.commentCount = post.getComments().size();
    }
} 