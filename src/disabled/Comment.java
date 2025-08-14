package com.meowtown.entity;

import com.meowtown.entity.enums.CommentTargetType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentTargetType targetType;
    
    @Column(nullable = false, columnDefinition = "uuid")
    private UUID targetId;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;
    
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> replies = new ArrayList<>();
    
    // Helper methods for polymorphic associations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_cat_id", insertable = false, updatable = false)
    private Cat targetCat;
    
    // Target post relationship removed - using new domain model
}