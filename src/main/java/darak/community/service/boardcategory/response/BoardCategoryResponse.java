package darak.community.service.boardcategory.response;

import darak.community.domain.board.BoardCategory;
import java.util.Objects;
import lombok.Getter;

@Getter
public class BoardCategoryResponse implements Comparable<BoardCategoryResponse> {

    private Long id;
    private String name;
    private Integer priority;
    private int boardCount;

    private BoardCategoryResponse(Long id, String name, Integer priority, int boardCount) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.boardCount = boardCount;
    }

    public static BoardCategoryResponse of(BoardCategory boardCategory) {
        return new BoardCategoryResponse(
                boardCategory.getId(),
                boardCategory.getName(),
                boardCategory.getPriority(),
                0
        );
    }

    public static BoardCategoryResponse of(BoardCategory boardCategory, int boardCount) {
        return new BoardCategoryResponse(
                boardCategory.getId(),
                boardCategory.getName(),
                boardCategory.getPriority(),
                boardCount
        );
    }

    @Override
    public int compareTo(BoardCategoryResponse o) {
        // null-safe 비교
        if (this.priority == null && o.priority == null) {
            return 0;
        }
        if (this.priority == null) {
            return 1;
        }
        if (o.priority == null) {
            return -1;
        }
        return this.priority.compareTo(o.priority);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BoardCategoryResponse that = (BoardCategoryResponse) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
