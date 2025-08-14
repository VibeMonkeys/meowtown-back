package com.meowtown.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cat_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatImage extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id", nullable = false)
    private Cat cat;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String imageUrl;
    
    @Builder.Default
    @Column(nullable = false)
    private Integer imageOrder = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;
}