package darak.community.domain.log;

import darak.community.domain.BaseEntity;
import darak.community.domain.comment.Comment;
import darak.community.domain.member.Member;
import darak.community.domain.post.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Getter
public class AdminLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private String reason;

    @Lob
    private String before;

    @Lob
    private String after;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    protected AdminLog(String type, String reason, String before, String after, Member member) {
        this.type = type;
        this.reason = reason;
        this.before = before;
        this.after = after;
        this.member = member;
    }

    public static AdminLog commentDeleteLog(Comment comment, Member member, String reason) {
        return new AdminLog("COMMENT_DELETE", reason, comment.getContent(), null, member);
    }

    public static AdminLog postDeleteLog(Post post, Member member, String reason) {
        return new AdminLog("POST_DELETE", reason, post.getContent(), null, member);
    }
}
