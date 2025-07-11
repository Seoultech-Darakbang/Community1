package darak.community.domain.comment;

import darak.community.domain.BaseEntity;
import darak.community.domain.member.Member;
import darak.community.domain.post.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private boolean anonymous;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 자식과 부모는 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;


    public static Comment createComment(String content, boolean anonymous, Post post, Member member) {
        Comment comment = new Comment();
        comment.content = content;
        comment.anonymous = anonymous;
        comment.post = post;
        comment.member = member;
        return comment;
    }

    public static Comment createReply(String content, boolean anonymous, Post post, Member member, Comment parent) {
        Comment comment = createComment(content, anonymous, post, member);
        comment.parent = parent;
        return comment;
    }

    public void updateParent(Comment parent) {
        this.parent = parent;
    }
    
}
