package darak.community.dto;

import darak.community.domain.Post;
import darak.community.domain.PostType;
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

    public PostCURequestForm(Post post) {
        this.title = post.getTitle();
        this.postType = post.getPostType();
        this.content = post.getContent();
        this.anonymous = post.getAnonymous();
    }
}
