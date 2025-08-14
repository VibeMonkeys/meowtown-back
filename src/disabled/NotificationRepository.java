package com.meowtown.repository;

import com.meowtown.entity.Notification;
import com.meowtown.entity.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    
    Page<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(UUID userId);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    Long countUnreadNotifications(@Param("userId") UUID userId);
    
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(UUID userId, NotificationType type);
    
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") UUID userId);
    
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :notificationId")
    int markAsRead(@Param("notificationId") UUID notificationId);
    
    void deleteByUserIdAndId(UUID userId, UUID notificationId);
    
    @Query("SELECT n FROM Notification n WHERE n.relatedId = :relatedId ORDER BY n.createdAt DESC")
    List<Notification> findByRelatedId(@Param("relatedId") UUID relatedId);
    
    void deleteByRelatedId(UUID relatedId);
}