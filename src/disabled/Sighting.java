package com.meowtown.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Entity
@Table(name = "sightings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sighting extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id", nullable = false)
    private Cat cat;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private User reporter;
    
    @Column(nullable = false, length = 200)
    private String location;
    
    @Column(columnDefinition = "geometry(Point,4326)")
    private Point coordinates;
    
    @Column(nullable = false)
    private LocalDateTime sightingTime;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(columnDefinition = "TEXT")
    private String imageUrl;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean isVerified = false;
}