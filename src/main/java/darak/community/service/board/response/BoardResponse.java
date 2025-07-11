package darak.community.service.board.response;

import darak.community.domain.board.Board;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BoardResponse {

    private Long boardId;

    private String name;

    private String description;

    private Integer priority;

    @Builder(access = AccessLevel.PRIVATE)
    private BoardResponse(Long boardId, String name, String description, Integer priority) {
        this.boardId = boardId;
        this.name = name;
        this.description = description;
        this.priority = priority;
    }

    public static BoardResponse of(Board board) {
        return BoardResponse.builder()
                .boardId(board.getId())
                .name(board.getName())
                .description(board.getDescription())
                .priority(board.getPriority())
                .build();
    }
}
