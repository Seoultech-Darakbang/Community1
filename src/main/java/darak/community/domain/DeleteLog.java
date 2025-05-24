package darak.community.domain;

import darak.community.domain.member.Member;
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
public class DeleteLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    protected DeleteLog(String content, Member member) {
        this.content = content;
        this.member = member;
    }

    public static DeleteLog postDeleteLog(Post post, Member member) {
        return new DeleteLog("title: " + post.getTitle() + "\n content: " + post.getContent(), member);
    }

    public static DeleteLog commentDeleteLog(Comment comment, Member member) {
        return new DeleteLog("content: " + comment.getContent(), member);
    }
}
