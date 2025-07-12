package darak.community.domain.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class BoardCategory implements Comparable<BoardCategory> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_category_id")
    private Long id;

    private String name;

    private Integer priority;

//    @OneToMany(mappedBy = "boardCategory")
//    private List<Board> boards = new ArrayList<>();
    // BoardCateegory를 별도의 Aggregate로 분리함.

    private BoardCategory(String name, Integer priority) {
        this.name = name;
        this.priority = priority;
    }

    public static BoardCategory create(String name, Integer priority) {
        return new BoardCategory(name, priority != null ? priority : 999);
    }

    @Override
    public int compareTo(BoardCategory o) {
        return this.priority - o.priority;
    }


    public void updateCategory(String name, Integer priority) {
        this.name = name;
        this.priority = priority != null ? priority : 999;
    }
}
