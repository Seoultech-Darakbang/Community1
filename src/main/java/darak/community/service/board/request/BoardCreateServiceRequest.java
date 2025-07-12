package darak.community.service.board.request;

import darak.community.domain.board.Board;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BoardCreateServiceRequest {

    private String name;

    private String description;

    private Integer priority;

    private Long boardCategoryId;

    @Builder
    private BoardCreateServiceRequest(String name, String description, Integer priority, Long boardCategoryId) {
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.boardCategoryId = boardCategoryId;
    }

    public Board toEntity() {
        return Board.builder()
                .name(name)
                .description(description)
                .priority(priority)
                .build();
    }
}
