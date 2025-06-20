package inhatc.fplist.service;

import inhatc.fplist.model.Level;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
public class LevelService {
    
    private List<Level> levels = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @PostConstruct
    public void loadLevels() {
        try {
            ClassPathResource resource = new ClassPathResource("fplist_data.json");
            levels = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<Level>>() {});
            System.out.println("Loaded " + levels.size() + " levels from JSON");
        } catch (IOException e) {
            System.err.println("Failed to load levels from JSON: " + e.getMessage());
            levels = new ArrayList<>();
        }
    }
    
    public List<Level> getAllLevels() {
        return new ArrayList<>(levels);
    }
    
    public List<Level> getAllLevelsSortedByRating() {
        return levels.stream()
                .sorted(Comparator.comparingInt(Level::getRating).reversed())
                .collect(Collectors.toList());
    }
    
    public long getTotalLevelCount() {
        return levels.size();
    }
    
    public List<Level> getLevelsByStatus(String statusCode) {
        if (statusCode == null || statusCode.isEmpty()) {
            return getAllLevelsSortedByRating();
        }
        return levels.stream()
                .filter(level -> level.getStatus() != null && 
                        statusCode.equalsIgnoreCase(level.getStatus().getCode()))
                .sorted(Comparator.comparingInt(Level::getRating).reversed())
                .collect(Collectors.toList());
    }
    
    public List<Level> searchLevels(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllLevelsSortedByRating();
        }
        String lowerQuery = query.toLowerCase().trim();
        return levels.stream()
                .filter(level -> level.getLevelName().toLowerCase().contains(lowerQuery))
                .sorted(Comparator.comparingInt(Level::getRating).reversed())
                .collect(Collectors.toList());
    }
} 