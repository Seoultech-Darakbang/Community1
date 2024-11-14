package darak.community.domain;

import darak.community.domain.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Entity
@Getter
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    // 대댓글(양방향 설계)
    // 자식과 부모는 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    // 대댓글(양방향 설계)
    // 부모와 자식은 1대다 관계
    @OneToMany(mappedBy = "parent")
    private List<Comment> child = new ArrayList<>();

    public void updateParent(Comment parent) {
        this.parent = parent;
    }

    // 연관관계 메서드
    public void addChildComment(Comment child) {
        this.child.add(child);
        child.updateParent(child);
    }
}
