package darak.community.service.board.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BoardUpdateServiceRequest {

    private Long boardId;

    private String name;

    private String description;

    private Integer priority;

    private Long boardCategoryId;

    @Builder
    private BoardUpdateServiceRequest(Long boardId, String name, String description, Integer priority,
                                      Long boardCategoryId) {
        this.boardId = boardId;
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.boardCategoryId = boardCategoryId;
    }
}
