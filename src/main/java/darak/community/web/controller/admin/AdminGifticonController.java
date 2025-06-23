package darak.community.web.controller.admin;

import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.dto.GifticonDto;
import darak.community.service.GifticonService;
import darak.community.web.argumentresolver.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/gifticons")
@RequiredArgsConstructor
@Slf4j
public class AdminGifticonController {

    private final GifticonService gifticonService;

    @ModelAttribute
    public void checkAdminAccess(@Login Member member, Model model) {
        if (member == null || (member.getMemberGrade() != MemberGrade.ADMIN
                && member.getMemberGrade() != MemberGrade.MASTER)) {
            log.error("관리자 접근 권한 없음");
            throw new IllegalAccessError("관리자만 접근 가능합니다.");
        }
        model.addAttribute("member", member);
        log.info("어드민 접근 허용");
    }

    @GetMapping
    public String adminGifticonList(@PageableDefault(size = 20) Pageable pageable, Model model) {
        log.info("=== 기프티콘 목록 조회 ===");
        Page<GifticonDto.Response> gifticons =
                gifticonService.getAllGifticons(pageable).map(GifticonDto.Response::from);
        model.addAttribute("gifticons", gifticons);
        model.addAttribute("active", "gifticons");
        log.info("기프티콘 목록 조회 완료: {} 개", gifticons.getTotalElements());
        return "admin/gifticon/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        log.info("=== 기프티콘 생성 폼 요청 ===");
        model.addAttribute("gifticon", new GifticonDto.CreateRequest());
        model.addAttribute("active", "gifticons");
        log.info("기프티콘 생성 폼 반환");
        return "admin/gifticon/create";
    }

    @PostMapping("/create")
    public String createGifticon(@ModelAttribute GifticonDto.CreateRequest request,
                                 RedirectAttributes redirectAttributes) {
        log.info("=== 기프티콘 생성 요청 받음 ===");
        log.info("Request data:");
        log.info("  - title: {}", request.getTitle());
        log.info("  - description: {}", request.getDescription());
        log.info("  - brand: {}", request.getBrand());
        log.info("  - totalQuantity: {}", request.getTotalQuantity());
        log.info("  - startTime: {}", request.getStartTime());
        log.info("  - endTime: {}", request.getEndTime());
        log.info("  - imageUrl: {}", request.getImageUrl());

        try {
            log.info("검증 시작...");
            if (request.getTotalQuantity() == null || request.getTotalQuantity() <= 0) {
                log.warn("총 수량 검증 실패: {}", request.getTotalQuantity());
                redirectAttributes.addFlashAttribute("error", "총 수량은 1 이상이어야 합니다.");
                return "redirect:/admin/gifticons/create";
            }

            if (request.getStartTime() == null || request.getEndTime() == null) {
                log.warn("시간 검증 실패 - startTime: {}, endTime: {}", request.getStartTime(), request.getEndTime());
                redirectAttributes.addFlashAttribute("error", "시작 시간과 종료 시간을 모두 입력해주세요.");
                return "redirect:/admin/gifticons/create";
            }

            if (request.getStartTime().isAfter(request.getEndTime())) {
                log.warn("시간 순서 검증 실패 - start: {}, end: {}", request.getStartTime(), request.getEndTime());
                redirectAttributes.addFlashAttribute("error", "시작 시간은 종료 시간보다 이전이어야 합니다.");
                return "redirect:/admin/gifticons/create";
            }

            log.info("검증 완료, 기프티콘 생성 서비스 호출...");
            Long gifticonId = gifticonService.createGifticon(request);
            log.info("기프티콘 생성 성공: ID = {}", gifticonId);
            redirectAttributes.addFlashAttribute("success", "기프티콘이 성공적으로 등록되었습니다.");

        } catch (Exception e) {
            log.error("기프티콘 생성 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "기프티콘 등록 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/admin/gifticons/create";
        }

        log.info("기프티콘 목록으로 리다이렉트");
        return "redirect:/admin/gifticons";
    }

    @PostMapping("/{id}/activate")
    public String activateGifticon(@PathVariable Long id) {
        log.info("기프티콘 활성화 요청: ID = {}", id);
        gifticonService.activateGifticon(id);
        return "redirect:/admin/gifticons";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivateGifticon(@PathVariable Long id) {
        log.info("기프티콘 비활성화 요청: ID = {}", id);
        gifticonService.deactivateGifticon(id);
        return "redirect:/admin/gifticons";
    }
} 