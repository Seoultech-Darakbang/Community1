package darak.community.domain.like;

import darak.community.domain.Post;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@DiscriminatorValue("P")
@Getter
public class PostHeart extends Heart {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
}
