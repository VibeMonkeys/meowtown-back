package com.meowtown.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/community")
@CrossOrigin(origins = "http://localhost:3001")
public class SimpleCommunityController {

    private static final List<Map<String, Object>> MOCK_POSTS = new ArrayList<>();
    private static final Map<String, List<Map<String, Object>>> MOCK_COMMENTS = new HashMap<>();
    
    static {
        // 목업 커뮤니티 게시글 데이터 생성
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> post = new HashMap<>();
            post.put("id", "post-" + i);
            post.put("author", "사용자" + i);
            post.put("content", "안녕하세요! 오늘 " + getRandomLocation() + "에서 귀여운 고양이를 목격했어요! 건강해 보였고 사람을 잘 따르더라구요. 혹시 주인을 찾고 계신 분이 있나요?");
            post.put("catName", "냥이" + i);
            post.put("location", getRandomLocation());
            post.put("time", getRandomTime(i));
            post.put("type", getRandomType());
            post.put("likes", (int)(Math.random() * 20) + 1);
            post.put("isLiked", false);
            post.put("comments", (int)(Math.random() * 10) + 1);
            post.put("isOwner", false);
            
            MOCK_POSTS.add(post);
            
            // 각 게시글에 대한 목업 댓글 생성
            List<Map<String, Object>> comments = new ArrayList<>();
            int commentCount = (int)(Math.random() * 5) + 1;
            for (int j = 1; j <= commentCount; j++) {
                Map<String, Object> comment = new HashMap<>();
                comment.put("id", "comment-" + i + "-" + j);
                comment.put("postId", "post-" + i);
                comment.put("author", "댓글러" + j);
                comment.put("content", "정보 감사합니다! 저도 그 근처에서 봤어요.");
                comment.put("time", getRandomTime(j + 10));
                comment.put("isOwner", false);
                comment.put("parentId", null);
                comment.put("replies", new ArrayList<>());
                
                comments.add(comment);
            }
            MOCK_COMMENTS.put("post-" + i, comments);
        }
    }
    
    private static String getRandomLocation() {
        String[] locations = {"강남역 3번 출구", "홍대입구역 근처", "이태원 공원", "성수동 카페거리", "건대 로데오거리"};
        return locations[(int)(Math.random() * locations.length)];
    }
    
    private static String getRandomTime(int offset) {
        if (offset <= 3) return offset + "시간 전";
        if (offset <= 10) return "어제";
        return (offset - 10) + "일 전";
    }
    
    private static String getRandomType() {
        String[] types = {"sighting", "help", "update"};
        return types[(int)(Math.random() * types.length)];
    }

    @GetMapping("/posts")
    public ResponseEntity<Map<String, Object>> getAllPosts(
            @RequestParam(defaultValue = "20") int size) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        
        // 최신 순으로 정렬하여 size만큼 반환
        List<Map<String, Object>> posts = new ArrayList<>(MOCK_POSTS);
        Collections.reverse(posts); // 최신 순
        
        if (posts.size() > size) {
            posts = posts.subList(0, size);
        }
        
        response.put("data", posts);
        response.put("message", posts.size() + "개의 게시글을 찾았습니다.");
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<Map<String, Object>> getPost(@PathVariable String postId) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<Map<String, Object>> post = MOCK_POSTS.stream()
                .filter(p -> postId.equals(p.get("id")))
                .findFirst();
        
        if (post.isPresent()) {
            response.put("success", true);
            response.put("data", post.get());
            response.put("message", "게시글을 찾았습니다.");
        } else {
            response.put("success", false);
            response.put("error", Map.of(
                "code", "NOT_FOUND",
                "message", "게시글을 찾을 수 없습니다."
            ));
        }
        
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Map<String, Object>> getPostComments(@PathVariable String postId) {
        Map<String, Object> response = new HashMap<>();
        
        List<Map<String, Object>> comments = MOCK_COMMENTS.getOrDefault(postId, new ArrayList<>());
        
        response.put("success", true);
        response.put("data", comments);
        response.put("message", comments.size() + "개의 댓글을 찾았습니다.");
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts")
    public ResponseEntity<Map<String, Object>> createPost(@RequestBody Map<String, Object> postData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 새 게시글 생성
            Map<String, Object> newPost = new HashMap<>();
            newPost.put("id", "post-" + UUID.randomUUID().toString().substring(0, 8));
            newPost.put("author", postData.getOrDefault("author", "익명"));
            newPost.put("content", postData.get("content"));
            newPost.put("catName", postData.get("catName"));
            newPost.put("location", postData.get("location"));
            newPost.put("time", "방금 전");
            newPost.put("type", postData.getOrDefault("type", "sighting"));
            newPost.put("likes", 0);
            newPost.put("isLiked", false);
            newPost.put("comments", 0);
            newPost.put("isOwner", true);
            
            // 목록에 추가 (최신이 앞에 오도록)
            MOCK_POSTS.add(0, newPost);
            
            // 빈 댓글 목록 초기화
            MOCK_COMMENTS.put((String) newPost.get("id"), new ArrayList<>());
            
            response.put("success", true);
            response.put("data", newPost);
            response.put("message", "게시글이 성공적으로 작성되었습니다.");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", Map.of(
                "code", "BAD_REQUEST",
                "message", "게시글 작성에 실패했습니다: " + e.getMessage()
            ));
        }
        
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Map<String, Object>> likePost(@PathVariable String postId) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<Map<String, Object>> post = MOCK_POSTS.stream()
                .filter(p -> postId.equals(p.get("id")))
                .findFirst();
        
        if (post.isPresent()) {
            Map<String, Object> postMap = post.get();
            boolean isLiked = (Boolean) postMap.get("isLiked");
            int likes = (Integer) postMap.get("likes");
            
            if (isLiked) {
                postMap.put("isLiked", false);
                postMap.put("likes", likes - 1);
            } else {
                postMap.put("isLiked", true);
                postMap.put("likes", likes + 1);
            }
            
            response.put("success", true);
            response.put("data", Map.of(
                "isLiked", postMap.get("isLiked"),
                "likes", postMap.get("likes")
            ));
            response.put("message", isLiked ? "좋아요를 취소했습니다." : "좋아요를 눌렀습니다.");
        } else {
            response.put("success", false);
            response.put("error", Map.of(
                "code", "NOT_FOUND",
                "message", "게시글을 찾을 수 없습니다."
            ));
        }
        
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Map<String, Object>> addComment(
            @PathVariable String postId, 
            @RequestBody Map<String, Object> commentData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 게시글 존재 확인
            Optional<Map<String, Object>> post = MOCK_POSTS.stream()
                    .filter(p -> postId.equals(p.get("id")))
                    .findFirst();
            
            if (!post.isPresent()) {
                response.put("success", false);
                response.put("error", Map.of(
                    "code", "NOT_FOUND",
                    "message", "게시글을 찾을 수 없습니다."
                ));
                return ResponseEntity.ok(response);
            }
            
            // 새 댓글 생성
            Map<String, Object> newComment = new HashMap<>();
            newComment.put("id", "comment-" + UUID.randomUUID().toString().substring(0, 8));
            newComment.put("postId", postId);
            newComment.put("author", commentData.getOrDefault("author", "익명"));
            newComment.put("content", commentData.get("content"));
            newComment.put("time", "방금 전");
            newComment.put("isOwner", false);
            newComment.put("parentId", commentData.get("parentId"));
            newComment.put("replies", new ArrayList<>());
            
            // 댓글 목록에 추가
            List<Map<String, Object>> comments = MOCK_COMMENTS.computeIfAbsent(postId, k -> new ArrayList<>());
            
            String parentId = (String) commentData.get("parentId");
            if (parentId != null) {
                // 대댓글인 경우
                Optional<Map<String, Object>> parentComment = comments.stream()
                        .filter(c -> parentId.equals(c.get("id")))
                        .findFirst();
                
                if (parentComment.isPresent()) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> replies = (List<Map<String, Object>>) parentComment.get().get("replies");
                    replies.add(newComment);
                }
            } else {
                // 일반 댓글인 경우
                comments.add(newComment);
            }
            
            // 게시글의 댓글 수 업데이트
            Map<String, Object> postMap = post.get();
            int commentCount = (Integer) postMap.get("comments");
            postMap.put("comments", commentCount + 1);
            
            response.put("success", true);
            response.put("data", newComment);
            response.put("message", "댓글이 성공적으로 작성되었습니다.");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", Map.of(
                "code", "BAD_REQUEST",
                "message", "댓글 작성에 실패했습니다: " + e.getMessage()
            ));
        }
        
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}