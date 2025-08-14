package com.meowtown.infrastructure.adapter.in.web;

import com.meowtown.application.mapper.CatMapper;
import com.meowtown.application.port.in.CreateCatUseCase;
import com.meowtown.application.port.in.FindCatUseCase;
import com.meowtown.common.ApiResponse;
import com.meowtown.domain.model.Cat;
import com.meowtown.domain.model.CatId;
import com.meowtown.domain.model.Coordinates;
import com.meowtown.domain.model.UserId;
import com.meowtown.dto.request.cat.CreateCatRequestDto;
import com.meowtown.dto.response.cat.CatResponseDto;
import com.meowtown.entity.enums.Gender;
import com.meowtown.entity.enums.LikeTargetType;
import com.meowtown.entity.Like;
import com.meowtown.entity.User;
import com.meowtown.repository.LikeRepository;
import com.meowtown.infrastructure.adapter.out.persistence.UserRepository;
import com.meowtown.application.port.out.CatRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Cat Web Controller (Inbound Adapter)
 * HTTP 요청을 받아 Use Case를 호출하는 웹 어댑터
 * 프론트엔드 기준 API 엔드포인트
 */
@RestController
@RequestMapping("/api/cats") // 프론트엔드 API 호출과 일치
public class CatController {
    
    private final CreateCatUseCase createCatUseCase;
    private final FindCatUseCase findCatUseCase;
    private final CatMapper catMapper;
    private final CatRepository catRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    
    public CatController(CreateCatUseCase createCatUseCase,
                        FindCatUseCase findCatUseCase,
                        CatMapper catMapper,
                        CatRepository catRepository,
                        LikeRepository likeRepository,
                        UserRepository userRepository) {
        this.createCatUseCase = createCatUseCase;
        this.findCatUseCase = findCatUseCase;
        this.catMapper = catMapper;
        this.catRepository = catRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * 새로운 고양이 등록
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CatResponseDto>> createCat(
            @Valid @RequestBody CreateCatRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // DTO -> Command 변환
        com.meowtown.application.port.in.CreateCatUseCase.CreateCatCommand command = catMapper.toCreateCommand(
            request.getName(),
            request.getDescription(),
            request.getEstimatedAge(),
            request.getGender(),
            request.isNeutered(),
            request.getLocation(),
            request.getLatitude(),
            request.getLongitude(),
            // TODO: 실제로는 JWT에서 추출한 인증된 사용자 ID 사용
            UserId.of(java.util.UUID.randomUUID()) // 임시 구현
        );
        
        // Use Case 실행
        Cat createdCat = createCatUseCase.createCat(command);
        
        // 이미지 Base64가 있으면 primaryImageUrl로 저장하고 다시 저장 (임시 처리)
        if (request.getImageBase64() != null && !request.getImageBase64().isEmpty()) {
            createdCat.setPrimaryImageUrl(request.getImageBase64());
            // 다시 저장해야 이미지가 반영됨
            createdCat = catRepository.save(createdCat);
        }
        
        // Domain -> DTO 변환
        CatResponseDto response = catMapper.toResponse(createdCat);
        
        return ResponseEntity.ok(ApiResponse.success(response, "고양이가 성공적으로 등록되었습니다."));
    }
    
    /**
     * 고양이 상세 조회
     */
    @GetMapping("/{catId}")
    public ResponseEntity<ApiResponse<CatResponseDto>> getCat(
            @PathVariable String catId) {
        CatId id = CatId.of(catId);
        
        return findCatUseCase.findCatById(id)
                .map(cat -> {
                    UUID userId = getOrCreateDefaultUser();
                    UUID catUuid = cat.getId().getValue();
                    Long likeCountLong = likeRepository.countByTargetTypeAndTargetId(LikeTargetType.CAT, catUuid);
                    int likeCount = likeCountLong != null ? likeCountLong.intValue() : 0;
                    boolean isLiked = likeRepository.existsByUserIdAndTargetTypeAndTargetId(userId, LikeTargetType.CAT, catUuid);
                    return ResponseEntity.ok(ApiResponse.success(catMapper.toResponse(cat, likeCount, isLiked)));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 고양이 목록 조회 (검색 및 필터링 포함)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CatResponseDto>>> getCats(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) Boolean isNeutered,
            @RequestParam(required = false, defaultValue = "true") Boolean isActive,
            @RequestParam(required = false) List<String> characteristics,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        // 파라미터 -> Query 변환
        FindCatUseCase.FindCatsQuery query = catMapper.toFindQuery(
            name, location, gender, isNeutered, isActive, characteristics,
            page, size, sortBy, sortDirection
        );
        
        // Use Case 실행
        List<Cat> cats = findCatUseCase.findCats(query);
        
        // 사용자별 좋아요 상태를 포함한 DTO 변환
        UUID userId = getOrCreateDefaultUser();
        List<CatResponseDto> response = cats.stream()
            .map(cat -> {
                UUID catId = cat.getId().getValue();
                Long likeCountLong = likeRepository.countByTargetTypeAndTargetId(LikeTargetType.CAT, catId);
                int likeCount = likeCountLong != null ? likeCountLong.intValue() : 0;
                boolean isLiked = likeRepository.existsByUserIdAndTargetTypeAndTargetId(userId, LikeTargetType.CAT, catId);
                return catMapper.toResponse(cat, likeCount, isLiked);
            })
            .toList();
        
        return ResponseEntity.ok(ApiResponse.success(response, 
            String.format("%d마리의 고양이를 찾았습니다.", response.size())));
    }
    
    /**
     * 주변 고양이 조회 (프론트엔드 기준 파라미터명)
     */
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<CatResponseDto>>> getNearbyCats(
            @RequestParam double lat,      // latitude → lat
            @RequestParam double lng,      // longitude → lng
            @RequestParam(defaultValue = "1000") double radius,
            @RequestParam(defaultValue = "20") int limit) {
        
        Coordinates center = Coordinates.of(lat, lng);
        
        // Use Case 실행
        List<Cat> nearbyCats = findCatUseCase.findNearbyCats(center, radius, limit);
        
        // 사용자별 좋아요 상태를 포함한 DTO 변환
        UUID userId = getOrCreateDefaultUser();
        List<CatResponseDto> response = nearbyCats.stream()
            .map(cat -> {
                UUID catId = cat.getId().getValue();
                Long likeCountLong = likeRepository.countByTargetTypeAndTargetId(LikeTargetType.CAT, catId);
                int likeCount = likeCountLong != null ? likeCountLong.intValue() : 0;
                boolean isLiked = likeRepository.existsByUserIdAndTargetTypeAndTargetId(userId, LikeTargetType.CAT, catId);
                return catMapper.toResponse(cat, likeCount, isLiked);
            })
            .toList();
        
        return ResponseEntity.ok(ApiResponse.success(response,
            String.format("반경 %.0fm 내에서 %d마리의 고양이를 찾았습니다.", radius, response.size())));
    }
    
    /**
     * 고양이 검색 (이름 기반)
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CatResponseDto>>> searchCats(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        FindCatUseCase.FindCatsQuery searchQuery = catMapper.toFindQuery(
            query, null, null, null, true, null,
            page, size, "createdAt", "desc"
        );
        
        List<Cat> cats = findCatUseCase.findCats(searchQuery);
        
        // 사용자별 좋아요 상태를 포함한 DTO 변환
        UUID userId = getOrCreateDefaultUser();
        List<CatResponseDto> response = cats.stream()
            .map(cat -> {
                UUID catId = cat.getId().getValue();
                Long likeCountLong = likeRepository.countByTargetTypeAndTargetId(LikeTargetType.CAT, catId);
                int likeCount = likeCountLong != null ? likeCountLong.intValue() : 0;
                boolean isLiked = likeRepository.existsByUserIdAndTargetTypeAndTargetId(userId, LikeTargetType.CAT, catId);
                return catMapper.toResponse(cat, likeCount, isLiked);
            })
            .toList();
        
        return ResponseEntity.ok(ApiResponse.success(response,
            String.format("'%s'로 검색된 %d마리의 고양이를 찾았습니다.", query, response.size())));
    }
    
    /**
     * 고양이 좋아요 토글
     */
    @PostMapping("/{catId}/like")
    public ResponseEntity<ApiResponse<CatLikeResponseDto>> toggleCatLike(
            @PathVariable String catId) {
        
        try {
            UUID catUuid = UUID.fromString(catId);
            
            // 임시로 기본 사용자 사용 (실제로는 JWT에서 추출)
            UUID userId = getOrCreateDefaultUser();
            
            // 기존 좋아요 확인
            Optional<Like> existingLike = likeRepository.findByUserIdAndTargetTypeAndTargetId(
                userId, LikeTargetType.CAT, catUuid);
            
            boolean isLiked;
            long likeCount;
            
            if (existingLike.isPresent()) {
                // 좋아요 취소
                likeRepository.delete(existingLike.get());
                isLiked = false;
            } else {
                // 좋아요 추가
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
                
                Like newLike = Like.builder()
                    .user(user)
                    .targetType(LikeTargetType.CAT)
                    .targetId(catUuid)
                    .build();
                
                likeRepository.save(newLike);
                isLiked = true;
            }
            
            // 총 좋아요 수 계산
            likeCount = likeRepository.countByTargetTypeAndTargetId(LikeTargetType.CAT, catUuid);
            
            CatLikeResponseDto response = CatLikeResponseDto.builder()
                .catId(catId)
                .isLiked(isLiked)
                .likeCount((int) likeCount)
                .build();
            
            String message = isLiked ? "좋아요를 추가했습니다." : "좋아요를 취소했습니다.";
            return ResponseEntity.ok(ApiResponse.success(response, message));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("좋아요 처리 중 오류가 발생했습니다: " + e.getMessage())
            );
        }
    }
    
    /**
     * 임시로 기본 사용자 ID 반환 (실제로는 JWT에서 추출)
     */
    private UUID getOrCreateDefaultUser() {
        // 기본 사용자가 없으면 생성
        return userRepository.findAll().stream()
            .findFirst()
            .map(User::getId)
            .orElseGet(() -> {
                User defaultUser = User.builder()
                    .username("guest")
                    .email("guest@example.com")
                    .displayName("게스트")
                    .passwordHash("temp") // 필수 필드
                    .build();
                return userRepository.save(defaultUser).getId();
            });
    }
    
    /**
     * 좋아요 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CatLikeResponseDto {
        private String catId;
        private Boolean isLiked;
        private Integer likeCount;
    }
}