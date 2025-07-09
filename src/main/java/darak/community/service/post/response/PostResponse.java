package darak.community.service.post.response;

import darak.community.domain.post.Post;
import darak.community.domain.post.PostType;
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
    private Long boardId;

    @Builder
    private PostResponse(String postId, String title, String content, Boolean anonymous, PostType postType,
                         Long authorId,
                         Long boardId) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.anonymous = anonymous;
        this.postType = postType;
        this.authorId = authorId;
        this.boardId = boardId;
    }

    public static PostResponse of(Post post) {
        return PostResponse.builder()
                .postId(post.getId().toString())
                .title(post.getTitle())
                .content(post.getContent())
                .anonymous(post.getAnonymous())
                .postType(post.getPostType())
                .authorId(post.getMember().getId())
                .boardId(post.getBoard().getId())
                .build();
    }
}
