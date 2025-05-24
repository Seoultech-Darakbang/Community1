package darak.community.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class BoardCategory implements Comparable<BoardCategory> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer priority;

    @OneToMany(mappedBy = "boardCategory")
    private List<Board> boards = new ArrayList<>();

    @Builder
    public BoardCategory(String name, Integer priority) {
        this.name = name;
        this.priority = priority;
    }

    @Override
    public int compareTo(BoardCategory o) {
        return this.priority - o.priority;
    }

    public void addBoard(Board board) {
        boards.add(board);
        board.setBoardCategory(this);
    }

    public Long getFirstBoardId() {
        return boards.getFirst().getId();
    }

    public void updateCategory(String name, Integer priority) {
        this.name = name;
        this.priority = priority != null ? priority : 999;
    }
}
