package darak.community.domain.like;

import darak.community.domain.Comment;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@DiscriminatorValue("C")
@Getter
public class CommentLike extends Like {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;
}
