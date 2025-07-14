package darak.community.service.board.response;

import darak.community.domain.board.Board;
import darak.community.service.boardcategory.response.BoardCategoryResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BoardAdminResponse implements Comparable<BoardAdminResponse> {

    private Long id;
    private String name;
    private String description;
    private Integer priority;
    private BoardCategoryResponse boardCategory;

    @Builder(access = AccessLevel.PRIVATE)
    private BoardAdminResponse(Long id, String name, String description,
                               Integer priority, BoardCategoryResponse boardCategory) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.boardCategory = boardCategory;
    }

    public static BoardAdminResponse of(Board board) {
        return BoardAdminResponse.builder()
                .id(board.getId())
                .name(board.getName())
                .description(board.getDescription())
                .priority(board.getPriority())
                .boardCategory(BoardCategoryResponse.of(board.getBoardCategory()))
                .build();
    }

    @Override
    public int compareTo(BoardAdminResponse o) {
        return this.priority.compareTo(o.priority);
    }
} 