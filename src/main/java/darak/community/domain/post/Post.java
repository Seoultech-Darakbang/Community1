package darak.community.domain.post;

import darak.community.domain.BaseEntity;
import darak.community.domain.board.Board;
import darak.community.domain.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private Long readCount;

    private Boolean anonymous;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;


    @Builder
    public Post(String title, String content, Boolean anonymous, PostType postType, Member member, Board board) {
        this.title = title;
        this.content = content;
        this.anonymous = anonymous;
        this.postType = postType;
        this.member = member;
        this.board = board;
        this.readCount = 0L;
    }

    public synchronized void increaseReadCount() {
        readCount++;
    }

    public int getViewCount() {
        return readCount != null ? readCount.intValue() : 0;
    }

    public void edit(String title, String content, PostType postType, Boolean anonymous) {
        this.title = title;
        this.content = content;
        this.postType = postType;
        this.anonymous = anonymous;
    }
}
