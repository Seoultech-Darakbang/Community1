package darak.community.service.board;

import darak.community.service.board.response.BoardResponse;
import darak.community.service.board.response.FavoriteServiceResponse;
import java.util.List;

public interface BoardFavoriteService {

    void addFavorite(Long memberId, Long boardId);

    void removeFavorite(Long memberId, Long boardId);

    FavoriteServiceResponse isFavorite(Long memberId, Long boardId);

    List<BoardResponse> findFavoriteBoardsBy(Long memberId);
}