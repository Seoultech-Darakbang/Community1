package darak.community.service;

import darak.community.domain.Board;
import darak.community.domain.BoardFavorite;
import darak.community.domain.member.Member;
import java.util.List;

public interface BoardFavoriteService {

    void addFavorite(Member member, Board board);

    void removeFavorite(Long memberId, Long boardId);

    List<BoardFavorite> findByMemberId(Long memberId);

    boolean isFavorite(Long memberId, Long boardId);
} 