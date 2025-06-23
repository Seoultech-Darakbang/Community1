package darak.community.domain.heart;

import darak.community.domain.comment.Comment;
import darak.community.domain.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"member_id", "comment_id"}
        )
)
public class CommentHeart extends Heart {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    public CommentHeart(Comment comment, Member member) {
        this.comment = comment;
        setMember(member);
        comment.getHearts().add(this);
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
