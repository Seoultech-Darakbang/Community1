package darak.community.service;

import darak.community.domain.Board;

public interface BoardService {
    Long save(Board board);
    Board findById(Long boardId);
    Board findByName(String name);

}
