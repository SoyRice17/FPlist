package inhatc.fplist.service;

import inhatc.fplist.model.Level;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
public class LevelService {
    
    // 추후 JSON 파일 로딩 로직이 들어갈 예정
    public List<Level> getAllLevels() {
        // 임시로 빈 리스트 반환
        return new ArrayList<>();
    }
    
    public long getTotalLevelCount() {
        return getAllLevels().size();
    }
    
    public List<Level> getLevelsByStatus(String statusCode) {
        // 추후 필터링 로직 구현 예정
        return new ArrayList<>();
    }
} 