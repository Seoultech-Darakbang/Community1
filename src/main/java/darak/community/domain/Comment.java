package darak.community.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Comment {
    @Id @GeneratedValue
    private Long id;

    private String content;

    @Embedded
    private TimeStamp timeStamp;

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
