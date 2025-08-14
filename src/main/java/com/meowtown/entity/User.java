package com.meowtown.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", unique = true, nullable = false, length = 50)
    private String userId;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;
    
    @Column(nullable = false)
    private String password; // 간단히 평문 저장 (실제 운영에서는 암호화 필요)
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // active 필드 제거 (현재 사용하지 않음)
}