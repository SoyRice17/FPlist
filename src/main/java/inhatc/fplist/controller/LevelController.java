package inhatc.fplist.controller;

import inhatc.fplist.model.Level;
import inhatc.fplist.service.LevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class LevelController {

    @Autowired
    private LevelService levelService;

    @GetMapping("/levels")
    public String levels(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            Model model) {
        
        List<Level> levels;
        
        // 검색어가 있으면 검색 우선
        if (search != null && !search.trim().isEmpty()) {
            levels = levelService.searchLevels(search);
            model.addAttribute("currentSearch", search);
        } 
        // 상태 필터가 있으면 필터링
        else if (status != null && !status.isEmpty()) {
            levels = levelService.getLevelsByStatus(status);
            model.addAttribute("currentStatus", status);
        } 
        // 기본: 전체 목록 (난이도순 정렬)
        else {
            levels = levelService.getAllLevelsSortedByRating();
        }
        
        model.addAttribute("levels", levels);
        model.addAttribute("totalCount", levelService.getTotalLevelCount());
        model.addAttribute("filteredCount", levels.size());
        
        return "levels";
    }
} 