package com.meowtown.entity;

import com.meowtown.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cat extends BaseEntity {
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(length = 50)
    private String estimatedAge;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private Gender gender = Gender.UNKNOWN;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean isNeutered = false;
    
    @Column(columnDefinition = "TEXT")
    private String primaryImageUrl;
    
    @Column(nullable = false, length = 200)
    private String location;
    
    @Column(columnDefinition = "geometry(Point,4326)")
    private Point coordinates;
    
    private LocalDateTime lastSeenAt;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by", referencedColumnName = "id")
    private User reportedBy;
    
    @OneToMany(mappedBy = "cat", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CatImage> images = new ArrayList<>();
    
    @OneToMany(mappedBy = "cat", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CatCharacteristic> characteristics = new ArrayList<>();
    
    @OneToMany(mappedBy = "cat", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Sighting> sightings = new ArrayList<>();
    
    // Community posts relationship removed - using new domain model
    
    @OneToMany(mappedBy = "targetCat", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
}