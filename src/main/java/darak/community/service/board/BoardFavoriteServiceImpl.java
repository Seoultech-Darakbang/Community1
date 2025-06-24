package darak.community.service.board;

import darak.community.domain.board.Board;
import darak.community.domain.board.BoardFavorite;
import darak.community.domain.member.Member;
import darak.community.infra.repository.BoardFavoriteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardFavoriteServiceImpl implements BoardFavoriteService {

    private final BoardFavoriteRepository boardFavoriteRepository;

    @Override
    @Transactional
    public void addFavorite(Member member, Board board) {
        if (isFavorite(member.getId(), board.getId())) {
            throw new IllegalStateException("이미 즐겨찾기에 추가된 게시판입니다.");
        }

        BoardFavorite boardFavorite = new BoardFavorite(member, board);

        int currentCount = boardFavoriteRepository.countByMemberId(member.getId());
        boardFavorite.setPriority(currentCount + 1);

        boardFavoriteRepository.save(boardFavorite);
    }

    @Override
    @Transactional
    public void removeFavorite(Long memberId, Long boardId) {
        BoardFavorite boardFavorite = boardFavoriteRepository.findByMemberIdAndBoardId(memberId, boardId)
                .orElseThrow(() -> new IllegalArgumentException("즐겨찾기에 추가되지 않은 게시판입니다."));

        boardFavoriteRepository.delete(boardFavorite);
    }

    @Override
    public List<BoardFavorite> findByMemberId(Long memberId) {
        return boardFavoriteRepository.findByMemberId(memberId);
    }

    @Override
    public boolean isFavorite(Long memberId, Long boardId) {
        return boardFavoriteRepository.findByMemberIdAndBoardId(memberId, boardId).isPresent();
    }
} 