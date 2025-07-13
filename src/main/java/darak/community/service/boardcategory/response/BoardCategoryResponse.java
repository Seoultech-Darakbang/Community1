package darak.community.service.boardcategory.response;

import darak.community.domain.board.BoardCategory;
import lombok.Getter;
import java.util.Objects;

@Getter
public class BoardCategoryResponse {

    private Long id;

    private String name;

    private Integer priority;

    private BoardCategoryResponse(Long id, String name, Integer priority) {
        this.id = id;
        this.name = name;   
        this.priority = priority;
    }

    public static BoardCategoryResponse of(BoardCategory boardCategory) {
        return new BoardCategoryResponse(boardCategory.getId(), boardCategory.getName(), boardCategory.getPriority());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardCategoryResponse that = (BoardCategoryResponse) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
