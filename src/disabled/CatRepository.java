package com.meowtown.repository;

import com.meowtown.entity.Cat;
import com.meowtown.entity.enums.Gender;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CatRepository extends JpaRepository<Cat, UUID>, JpaSpecificationExecutor<Cat> {
    
    Page<Cat> findByIsActiveTrue(Pageable pageable);
    
    Page<Cat> findByIsActiveTrueAndLocationContainingIgnoreCase(String location, Pageable pageable);
    
    Page<Cat> findByIsActiveTrueAndGender(Gender gender, Pageable pageable);
    
    Page<Cat> findByIsActiveTrueAndIsNeutered(Boolean isNeutered, Pageable pageable);
    
    @Query("SELECT c FROM Cat c WHERE c.isActive = true AND c.name LIKE %:name%")
    Page<Cat> findByNameContainingIgnoreCaseAndIsActiveTrue(@Param("name") String name, Pageable pageable);
    
    // PostGIS 쿼리는 나중에 구현
    // @Query("SELECT c FROM Cat c WHERE c.isActive = true AND " +
    //        "ST_DWithin(c.coordinates, ST_GeomFromText(:point, 4326), :radiusInMeters) = true")
    // List<Cat> findCatsWithinRadius(@Param("point") String point, @Param("radiusInMeters") double radiusInMeters);
    
    // @Query("SELECT c FROM Cat c WHERE c.isActive = true AND " +
    //        "ST_Within(c.coordinates, ST_MakeEnvelope(:minLng, :minLat, :maxLng, :maxLat, 4326)) = true")
    // List<Cat> findCatsWithinBounds(@Param("minLng") double minLng, @Param("minLat") double minLat,
    //                                @Param("maxLng") double maxLng, @Param("maxLat") double maxLat);
    
    @Query("SELECT c FROM Cat c JOIN c.characteristics ch WHERE c.isActive = true AND " +
           "ch.characteristic IN :characteristics")
    Page<Cat> findByCharacteristicsIn(@Param("characteristics") List<String> characteristics, Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Cat c WHERE c.isActive = true")
    Long countActiveCats();
    
    @Query("SELECT COUNT(c) FROM Cat c WHERE c.isActive = true AND c.createdAt >= :startDate")
    Long countNewCatsAfter(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT COUNT(c) FROM Cat c WHERE c.isActive = true AND c.isNeutered = true")
    Long countNeuteredCats();
    
    List<Cat> findByReportedById(UUID userId);
    
    // Hexagonal Architecture를 위한 추가 메서드들
    List<Cat> findByNameContainingIgnoreCase(String name);
    
    // PostGIS 쿼리는 나중에 구현
    // @Query("SELECT c FROM Cat c WHERE c.isActive = true AND " +
    //        "ST_DWithin(c.coordinates, :centerPoint, :radiusInMeters) = true ORDER BY " +
    //        "ST_Distance(c.coordinates, :centerPoint)")
    // List<Cat> findNearbyActiveCats(@Param("centerPoint") Point centerPoint, 
    //                                @Param("radiusInMeters") double radiusInMeters, 
    //                                Pageable pageable);
    
    // @Query("SELECT c FROM Cat c WHERE " +
    //        "ST_DWithin(c.coordinates, :centerPoint, :radiusInMeters) = true")
    // List<Cat> findByLocationNear(@Param("centerPoint") Point centerPoint, 
    //                             @Param("radiusInMeters") double radiusInMeters);
    
    long countByIsActiveTrue();
}