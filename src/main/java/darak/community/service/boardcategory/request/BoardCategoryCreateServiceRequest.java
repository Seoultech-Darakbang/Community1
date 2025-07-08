package darak.community.service.boardcategory.request;

import darak.community.domain.board.BoardCategory;
import lombok.Getter;

@Getter
public class BoardCategoryCreateServiceRequest {

    private String name;

    private Integer priority;

    private BoardCategoryCreateServiceRequest(String name, Integer priority) {
        this.name = name;
        this.priority = priority;
    }

    public static BoardCategoryCreateServiceRequest of(String name, Integer priority) {
        return new BoardCategoryCreateServiceRequest(name, priority);
    }

    public BoardCategory toEntity() {
        return BoardCategory.create(name, priority);
    }
}
