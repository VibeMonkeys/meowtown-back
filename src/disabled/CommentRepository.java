package com.meowtown.repository;

import com.meowtown.entity.Comment;
import com.meowtown.entity.enums.CommentTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    
    List<Comment> findByTargetTypeAndTargetIdAndParentCommentIsNullOrderByCreatedAtAsc(
            CommentTargetType targetType, UUID targetId);
    
    Page<Comment> findByTargetTypeAndTargetIdAndParentCommentIsNull(
            CommentTargetType targetType, UUID targetId, Pageable pageable);
    
    List<Comment> findByParentCommentIdOrderByCreatedAtAsc(UUID parentCommentId);
    
    List<Comment> findByAuthorIdOrderByCreatedAtDesc(UUID authorId);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.targetType = :targetType AND c.targetId = :targetId")
    Long countByTargetTypeAndTargetId(@Param("targetType") CommentTargetType targetType, 
                                     @Param("targetId") UUID targetId);
    
    @Query("SELECT c FROM Comment c WHERE c.content LIKE %:keyword% ORDER BY c.createdAt DESC")
    Page<Comment> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT c.targetId, COUNT(c) FROM Comment c WHERE c.targetType = :targetType " +
           "AND c.targetId IN :targetIds GROUP BY c.targetId")
    List<Object[]> countCommentsForTargets(@Param("targetType") CommentTargetType targetType,
                                          @Param("targetIds") List<UUID> targetIds);
    
    void deleteByTargetTypeAndTargetId(CommentTargetType targetType, UUID targetId);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.parentComment.id = :parentCommentId")
    Long countReplies(@Param("parentCommentId") UUID parentCommentId);
}