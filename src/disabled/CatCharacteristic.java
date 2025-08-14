package com.meowtown.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cat_characteristics", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"cat_id", "characteristic"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatCharacteristic extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id", nullable = false)
    private Cat cat;
    
    @Column(nullable = false, length = 50)
    private String characteristic;
}