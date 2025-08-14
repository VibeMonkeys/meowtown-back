package com.meowtown.entity;

import com.meowtown.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(nullable = false)
    private String passwordHash;
    
    @Column(length = 100)
    private String displayName;
    
    @Column(columnDefinition = "TEXT")
    private String avatarUrl;
    
    @Column(length = 20)
    private String phoneNumber;
    
    @Column(length = 200)
    private String location;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean isVerified = false;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private UserRole role = UserRole.USER;
    
    @OneToMany(mappedBy = "reportedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Cat> reportedCats = new ArrayList<>();
    
    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Sighting> sightings = new ArrayList<>();
    
    // Community posts relationship removed - using new domain model
    
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Like> likes = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();
}