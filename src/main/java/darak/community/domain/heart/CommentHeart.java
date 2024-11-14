package darak.community.domain.heart;

import darak.community.domain.Comment;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

@Entity
@Getter
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"member_id", "comment_id"}
        )
)
public class CommentHeart extends Heart {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;
}
