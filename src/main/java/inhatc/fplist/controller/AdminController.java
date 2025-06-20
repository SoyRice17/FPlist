package inhatc.fplist.controller;

import inhatc.fplist.model.Level;
import inhatc.fplist.service.LevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private LevelService levelService;

    /**
     * 관리자 로그인 페이지
     */
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMsg", "아이디 또는 비밀번호가 잘못되었습니다.");
        }
        return "admin/login";
    }

    /**
     * 관리자 대시보드
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalLevels", levelService.getTotalLevelCount());
        model.addAttribute("recentLevels", levelService.getAllLevelsSortedByRating().stream().limit(10).toList());
        return "admin/dashboard";
    }

    /**
     * 레벨 목록 관리 페이지
     */
    @GetMapping("/levels")
    public String manageLevels(Model model) {
        model.addAttribute("levels", levelService.getAllLevelsSortedByRating());
        return "admin/levels";
    }

    /**
     * 새 레벨 추가 페이지
     */
    @GetMapping("/levels/add")
    public String addLevelForm(Model model) {
        model.addAttribute("level", new Level());
        model.addAttribute("isEdit", false);
        return "admin/level-form";
    }

    /**
     * 레벨 수정 페이지
     */
    @GetMapping("/levels/edit/{levelId}")
    public String editLevelForm(@PathVariable String levelId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Level> level = levelService.getLevelById(levelId);
        if (level.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "레벨을 찾을 수 없습니다.");
            return "redirect:/admin/levels";
        }
        
        model.addAttribute("level", level.get());
        model.addAttribute("isEdit", true);
        return "admin/level-form";
    }

    /**
     * 레벨 추가 처리
     */
    @PostMapping("/levels/add")
    public String addLevel(@ModelAttribute Level level, RedirectAttributes redirectAttributes) {
        try {
            // 상태 객체가 null인 경우 기본값 설정
            if (level.getStatus() == null) {
                level.setStatus(new Level.Status());
            }
            
            // FPS 데이터가 null인 경우 기본값 설정
            if (level.getFpsData() == null) {
                level.setFpsData(new Level.FpsData());
            }
            
            boolean success = levelService.addLevel(level);
            if (success) {
                redirectAttributes.addFlashAttribute("success", "레벨이 성공적으로 추가되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("error", "이미 존재하는 레벨 ID입니다.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "레벨 추가 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return "redirect:/admin/levels";
    }

    /**
     * 레벨 수정 처리
     */
    @PostMapping("/levels/edit/{levelId}")
    public String updateLevel(@PathVariable String levelId, @ModelAttribute Level level, 
                            RedirectAttributes redirectAttributes) {
        try {
            // 상태 객체가 null인 경우 기본값 설정
            if (level.getStatus() == null) {
                level.setStatus(new Level.Status());
            }
            
            // FPS 데이터가 null인 경우 기본값 설정
            if (level.getFpsData() == null) {
                level.setFpsData(new Level.FpsData());
            }
            
            boolean success = levelService.updateLevel(levelId, level);
            if (success) {
                redirectAttributes.addFlashAttribute("success", "레벨이 성공적으로 수정되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("error", "레벨을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "레벨 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return "redirect:/admin/levels";
    }

    /**
     * 레벨 삭제 처리
     */
    @PostMapping("/levels/delete/{levelId}")
    public String deleteLevel(@PathVariable String levelId, RedirectAttributes redirectAttributes) {
        try {
            boolean success = levelService.deleteLevel(levelId);
            if (success) {
                redirectAttributes.addFlashAttribute("success", "레벨이 성공적으로 삭제되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("error", "레벨을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "레벨 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return "redirect:/admin/levels";
    }

    /**
     * 백업 생성
     */
    @PostMapping("/backup")
    public String createBackup(RedirectAttributes redirectAttributes) {
        try {
            boolean success = levelService.createBackup();
            if (success) {
                redirectAttributes.addFlashAttribute("success", "백업이 성공적으로 생성되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("error", "백업 생성에 실패했습니다.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "백업 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return "redirect:/admin/dashboard";
    }

    /**
     * REST API - 레벨 정보 조회 (AJAX용)
     */
    @GetMapping("/api/levels/{levelId}")
    @ResponseBody
    public Level getLevelApi(@PathVariable String levelId) {
        return levelService.getLevelById(levelId).orElse(null);
    }
    
    /**
     * 로그아웃 상태 테스트용 (개발용)
     */
    @GetMapping("/logout-test")
    @ResponseBody
    public String logoutTest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        return String.format(
            "세션 존재: %s, 인증 상태: %s, 사용자명: %s", 
            session != null, 
            auth != null && auth.isAuthenticated(),
            auth != null ? auth.getName() : "없음"
        );
    }
} 