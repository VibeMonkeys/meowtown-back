package com.meowtown.controller;

import com.meowtown.entity.User;
import com.meowtown.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/cats")
@CrossOrigin(origins = "http://localhost:3001")
public class SimpleCatController {

    private static final List<Map<String, Object>> MOCK_CATS = new ArrayList<>();
    
    static {
        // 목업 고양이 데이터 생성
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> cat = new HashMap<>();
            cat.put("id", "cat-" + i);
            cat.put("name", "냥이" + i);
            cat.put("image", "https://picsum.photos/300/200?random=" + i);
            cat.put("location", "서울시 강남구");
            cat.put("lastSeen", (i == 1) ? "방금 전" : i + "시간 전");
            cat.put("description", "귀여운 고양이입니다 🐱");
            cat.put("characteristics", Arrays.asList("친근함", "활발함", "귀여움"));
            
            Map<String, Object> reportedBy = new HashMap<>();
            reportedBy.put("name", "사용자" + i);
            reportedBy.put("avatar", null);
            cat.put("reportedBy", reportedBy);
            
            cat.put("likes", (int)(Math.random() * 20) + 1);
            cat.put("comments", (int)(Math.random() * 10) + 1);
            cat.put("isLiked", false);
            cat.put("isNeutered", Math.random() > 0.5);
            cat.put("estimatedAge", "1-2년");
            cat.put("gender", new String[]{"male", "female", "unknown"}[(int)(Math.random() * 3)]);
            cat.put("lat", 37.4979 + (Math.random() * 0.1));
            cat.put("lng", 127.0276 + (Math.random() * 0.1));
            cat.put("reportCount", (int)(Math.random() * 5) + 1);
            
            MOCK_CATS.add(cat);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCats() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", MOCK_CATS);
        response.put("message", MOCK_CATS.size() + "마리의 고양이를 찾았습니다.");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCatById(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<Map<String, Object>> cat = MOCK_CATS.stream()
            .filter(c -> id.equals(c.get("id")))
            .findFirst();
            
        if (cat.isPresent()) {
            response.put("success", true);
            response.put("data", cat.get());
            response.put("message", "고양이 정보를 찾았습니다.");
        } else {
            response.put("success", false);
            response.put("error", Map.of("code", "NOT_FOUND", "message", "고양이를 찾을 수 없습니다."));
        }
        
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCat(@RequestBody Map<String, Object> catData, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        // 로그인 확인
        User currentUser = SessionUtil.getCurrentUser(session);
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(401).body(response);
        }
        
        // 새 고양이 생성
        Map<String, Object> newCat = new HashMap<>(catData);
        newCat.put("id", "cat-" + System.currentTimeMillis());
        newCat.put("likes", 0);
        newCat.put("comments", 0);
        newCat.put("isLiked", false);
        newCat.put("reportCount", 1);
        
        // 등록자 정보 설정
        Map<String, Object> reportedBy = new HashMap<>();
        reportedBy.put("name", currentUser.getDisplayName());
        reportedBy.put("avatar", null);
        newCat.put("reportedBy", reportedBy);
        
        MOCK_CATS.add(0, newCat); // 맨 앞에 추가
        
        response.put("success", true);
        response.put("data", newCat);
        response.put("message", "고양이가 성공적으로 등록되었습니다!");
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCats(@RequestParam String query) {
        Map<String, Object> response = new HashMap<>();
        
        List<Map<String, Object>> results = MOCK_CATS.stream()
            .filter(cat -> cat.get("name").toString().toLowerCase().contains(query.toLowerCase()))
            .toList();
            
        response.put("success", true);
        response.put("data", results);
        response.put("message", results.size() + "마리의 고양이를 찾았습니다.");
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleCatLike(@PathVariable String id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        // 로그인 확인
        User currentUser = SessionUtil.getCurrentUser(session);
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(401).body(response);
        }
        
        // 고양이 찾기
        Optional<Map<String, Object>> catOpt = MOCK_CATS.stream()
            .filter(cat -> cat.get("id").equals(id))
            .findFirst();
            
        if (catOpt.isPresent()) {
            Map<String, Object> cat = catOpt.get();
            boolean isLiked = (Boolean) cat.get("isLiked");
            int likes = (Integer) cat.get("likes");
            
            // 좋아요 토글
            boolean newIsLiked = !isLiked;
            int newLikes = newIsLiked ? likes + 1 : likes - 1;
            
            cat.put("isLiked", newIsLiked);
            cat.put("likes", newLikes);
            
            Map<String, Object> result = new HashMap<>();
            result.put("catId", id);
            result.put("isLiked", newIsLiked);
            result.put("likeCount", newLikes);
            
            response.put("success", true);
            response.put("data", result);
            response.put("message", newIsLiked ? "좋아요를 눌렀습니다." : "좋아요를 취소했습니다.");
        } else {
            response.put("success", false);
            response.put("message", "고양이를 찾을 수 없습니다.");
        }
        
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}