package inhatc.fplist.service;

import inhatc.fplist.model.Level;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class LevelService {
    
    private List<Level> levels = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String jsonFilePath;
    
    // 동시성 제어를 위한 읽기/쓰기 락
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    @PostConstruct
    public void loadLevels() {
        try {
            ClassPathResource resource = new ClassPathResource("fplist_data.json");
            
            // JSON 파일 경로 저장 (쓰기용)
            try {
                File file = ResourceUtils.getFile("classpath:fplist_data.json");
                jsonFilePath = file.getAbsolutePath();
            } catch (Exception e) {
                // 배포 환경에서는 resources 폴더에 직접 쓸 수 없으므로 임시 경로 사용
                jsonFilePath = System.getProperty("java.io.tmpdir") + "/fplist_data.json";
                // 초기 데이터를 임시 파일로 복사
                copyResourceToFile(resource, jsonFilePath);
            }
            
            levels = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<Level>>() {});
            System.out.println("Loaded " + levels.size() + " levels from JSON");
        } catch (IOException e) {
            System.err.println("Failed to load levels from JSON: " + e.getMessage());
            levels = new ArrayList<>();
        }
    }
    
    private void copyResourceToFile(ClassPathResource resource, String targetPath) throws IOException {
        byte[] data = resource.getInputStream().readAllBytes();
        Files.write(Paths.get(targetPath), data);
    }
    
    // 기존 읽기 메서드들
    public List<Level> getAllLevels() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(levels);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public List<Level> getAllLevelsSortedByRating() {
        lock.readLock().lock();
        try {
            return levels.stream()
                    .sorted(Comparator.comparingInt(Level::getRating).reversed())
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public long getTotalLevelCount() {
        lock.readLock().lock();
        try {
            return levels.size();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public List<Level> getLevelsByStatus(String statusCode) {
        lock.readLock().lock();
        try {
            if (statusCode == null || statusCode.isEmpty()) {
                return getAllLevelsSortedByRating();
            }
            return levels.stream()
                    .filter(level -> level.getStatus() != null && 
                            statusCode.equalsIgnoreCase(level.getStatus().getCode()))
                    .sorted(Comparator.comparingInt(Level::getRating).reversed())
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public List<Level> searchLevels(String query) {
        lock.readLock().lock();
        try {
            if (query == null || query.trim().isEmpty()) {
                return getAllLevelsSortedByRating();
            }
            String lowerQuery = query.toLowerCase().trim();
            return levels.stream()
                    .filter(level -> level.getLevelName().toLowerCase().contains(lowerQuery))
                    .sorted(Comparator.comparingInt(Level::getRating).reversed())
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    // 새로운 관리자 기능들
    
    /**
     * 레벨 추가
     */
    public boolean addLevel(Level level) {
        lock.writeLock().lock();
        try {
            // 중복 체크 (레벨 ID 기준)
            boolean exists = levels.stream()
                    .anyMatch(l -> l.getLevelId().equals(level.getLevelId()));
            
            if (exists) {
                return false; // 이미 존재하는 레벨
            }
            
            // Rating 자동 계산
            calculateRating(level);
            
            levels.add(level);
            saveToJsonFile();
            System.out.println("Level added: " + level.getLevelName());
            return true;
        } catch (Exception e) {
            System.err.println("Failed to add level: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 레벨 수정
     */
    public boolean updateLevel(String levelId, Level updatedLevel) {
        lock.writeLock().lock();
        try {
            Optional<Level> existingLevel = levels.stream()
                    .filter(l -> l.getLevelId().equals(levelId))
                    .findFirst();
            
            if (existingLevel.isEmpty()) {
                return false; // 존재하지 않는 레벨
            }
            
            Level level = existingLevel.get();
            
            // 데이터 업데이트
            level.setLevelName(updatedLevel.getLevelName());
            level.setLevelLink(updatedLevel.getLevelLink());
            level.setFpsData(updatedLevel.getFpsData());
            level.setStatus(updatedLevel.getStatus());
            level.setComment(updatedLevel.getComment());
            
            // Rating 재계산
            calculateRating(level);
            
            saveToJsonFile();
            System.out.println("Level updated: " + level.getLevelName());
            return true;
        } catch (Exception e) {
            System.err.println("Failed to update level: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 레벨 삭제
     */
    public boolean deleteLevel(String levelId) {
        lock.writeLock().lock();
        try {
            boolean removed = levels.removeIf(l -> l.getLevelId().equals(levelId));
            
            if (removed) {
                saveToJsonFile();
                System.out.println("Level deleted: " + levelId);
            }
            
            return removed;
        } catch (Exception e) {
            System.err.println("Failed to delete level: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * ID로 레벨 찾기
     */
    public Optional<Level> getLevelById(String levelId) {
        lock.readLock().lock();
        try {
            return levels.stream()
                    .filter(l -> l.getLevelId().equals(levelId))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Rating 자동 계산
     * Rating = fps_60 + (fps_120 × 2) + (fps_240 × 4)
     */
    private void calculateRating(Level level) {
        if (level.getFpsData() != null) {
            int rating = level.getFpsData().getFps60() + 
                        (level.getFpsData().getFps120() * 2) + 
                        (level.getFpsData().getFps240() * 4);
            level.setRating(rating);
        }
    }
    
    /**
     * JSON 파일에 저장
     */
    private void saveToJsonFile() throws IOException {
        try (FileWriter writer = new FileWriter(jsonFilePath)) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, levels);
        }
    }
    
    /**
     * 백업 생성
     */
    public boolean createBackup() {
        lock.readLock().lock();
        try {
            String backupPath = jsonFilePath + ".backup." + System.currentTimeMillis();
            try (FileWriter writer = new FileWriter(backupPath)) {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, levels);
            }
            System.out.println("Backup created: " + backupPath);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to create backup: " + e.getMessage());
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }
} 