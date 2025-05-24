package darak.community.domain;

import jakarta.persistence.Entity;
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
public class Board implements Comparable<Board> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;
    
    private Integer priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_category_id")
    private BoardCategory boardCategory;

    @Builder
    public Board(String name, String description, BoardCategory boardCategory, Integer priority) {
        this.name = name;
        this.description = description;
        this.boardCategory = boardCategory;
        this.priority = priority != null ? priority : 999;
    }

    public void setBoardCategory(BoardCategory boardCategory) {
        this.boardCategory = boardCategory;
        boardCategory.addBoard(this);
    }

    public void updateBoard(String name, String description, BoardCategory boardCategory, Integer priority) {
        this.name = name;
        this.description = description;
        this.boardCategory = boardCategory;
        this.priority = priority != null ? priority : 999;
    }

    @Override
    public int compareTo(Board o) {
        return this.priority.compareTo(o.priority);
    }
}
