package darak.community.web.controllerAdvice;

import darak.community.core.argumentresolver.Login;
import darak.community.core.session.dto.LoginMember;
import darak.community.service.board.BoardService;
import darak.community.service.board.response.BoardResponse;
import darak.community.service.boardcategory.response.BoardCategoryResponse;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "darak.community.web.controller.community")
@RequiredArgsConstructor
public class CommunityControllerAdvice {

    private final BoardService boardService;

    @ModelAttribute
    public void addBasicAttributes(@Login LoginMember loginMember, Model model) {
        addBoardInformation(model);
        model.addAttribute("member", loginMember);
    }

    private void addBoardInformation(Model model) {
        Map<BoardCategoryResponse, List<BoardResponse>> boardsGroupedByCategory = boardService.findBoardsGroupedByCategory();
        model.addAttribute("boardsGroupedByCategory", boardsGroupedByCategory);
    }
}
