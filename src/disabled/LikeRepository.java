package com.meowtown.repository;

import com.meowtown.entity.Like;
import com.meowtown.entity.enums.LikeTargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {
    
    Optional<Like> findByUserIdAndTargetTypeAndTargetId(UUID userId, LikeTargetType targetType, UUID targetId);
    
    List<Like> findByTargetTypeAndTargetId(LikeTargetType targetType, UUID targetId);
    
    @Query("SELECT COUNT(l) FROM Like l WHERE l.targetType = :targetType AND l.targetId = :targetId")
    Long countByTargetTypeAndTargetId(@Param("targetType") LikeTargetType targetType, @Param("targetId") UUID targetId);
    
    boolean existsByUserIdAndTargetTypeAndTargetId(UUID userId, LikeTargetType targetType, UUID targetId);
    
    void deleteByUserIdAndTargetTypeAndTargetId(UUID userId, LikeTargetType targetType, UUID targetId);
    
    @Query("SELECT l.targetId, COUNT(l) FROM Like l WHERE l.targetType = :targetType " +
           "AND l.targetId IN :targetIds GROUP BY l.targetId")
    List<Object[]> countLikesForTargets(@Param("targetType") LikeTargetType targetType, 
                                       @Param("targetIds") List<UUID> targetIds);
    
    List<Like> findByUserId(UUID userId);
}