package darak.community.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Entity
@Getter
public class BoardCategory implements Comparable<BoardCategory> {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Integer priority;

    @OneToMany(mappedBy = "boardCategory")
    private List<Board> boards = new ArrayList<>();

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
}
