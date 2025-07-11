package darak.community.service.post.response;

import darak.community.domain.post.Post;
import darak.community.domain.post.PostType;
import darak.community.service.board.response.BoardResponse;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostResponse {

    private String postId;
    private String title;
    private String content;
    private Boolean anonymous;
    private PostType postType;
    private Long authorId;
    private BoardResponse boardResponse;
    private Long readCount;
    private LocalDateTime createdDate;
    
    @Builder
    private PostResponse(String postId, String title, String content, Boolean anonymous,
                         PostType postType, Long authorId, BoardResponse boardResponse,
                         Long readCount, LocalDateTime createdDate) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.anonymous = anonymous;
        this.postType = postType;
        this.authorId = authorId;
        this.boardResponse = boardResponse;
        this.readCount = readCount;
        this.createdDate = createdDate;
    }

    public static PostResponse of(Post post) {
        return PostResponse.builder()
                .postId(post.getId().toString())
                .title(post.getTitle())
                .content(post.getContent())
                .anonymous(post.getAnonymous())
                .postType(post.getPostType())
                .authorId(post.getMember().getId())
                .boardResponse(BoardResponse.of(post.getBoard()))
                .readCount(post.getReadCount())
                .createdDate(post.getCreatedDate())
                .build();
    }
}
