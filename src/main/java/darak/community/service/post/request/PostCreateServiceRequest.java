package darak.community.service.post.request;

import darak.community.domain.post.PostType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostCreateServiceRequest {

    private String title;
    private String content;
    private Boolean anonymous;
    private PostType postType;
    private Long authorId;
    private Long boardId;

    @Builder
    private PostCreateServiceRequest(String title, String content, Boolean anonymous, PostType postType, Long authorId,
                                     Long boardId) {
        this.title = title;
        this.content = content;
        this.anonymous = anonymous;
        this.postType = postType;
        this.authorId = authorId;
        this.boardId = boardId;
    }
}
