package darak.community.web.dto;

import darak.community.domain.post.PostType;
import darak.community.service.post.request.PostCreateServiceRequest;
import darak.community.service.post.request.PostUpdateServiceRequest;
import darak.community.service.post.response.PostResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCURequestForm {

    @NotEmpty
    private String title;

    @NotNull
    private PostType postType;

    @NotEmpty
    private String content;

    private Boolean anonymous;

    public PostCURequestForm(PostResponse post) {
        this.title = post.getTitle();
        this.postType = post.getPostType();
        this.content = post.getContent();
        this.anonymous = post.getAnonymous();
    }

    public PostCreateServiceRequest toCreateServiceRequest(Long authorId, Long boardId) {
        return PostCreateServiceRequest.builder()
                .title(title)
                .postType(postType)
                .content(content)
                .anonymous(anonymous)
                .authorId(authorId)
                .boardId(boardId)
                .build();
    }

    public PostUpdateServiceRequest toUpdateServiceRequest(Long postId, Long authorId, Long boardId) {
        return PostUpdateServiceRequest.builder()
                .postId(postId)
                .title(title)
                .postType(postType)
                .content(content)
                .anonymous(anonymous)
                .authorId(authorId)
                .boardId(boardId)
                .build();
    }
}
