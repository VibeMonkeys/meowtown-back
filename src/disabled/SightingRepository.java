package com.meowtown.repository;

import com.meowtown.entity.Sighting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SightingRepository extends JpaRepository<Sighting, UUID> {
    
    Page<Sighting> findAll(Pageable pageable);
    
    Page<Sighting> findByCatId(UUID catId, Pageable pageable);
    
    List<Sighting> findByCatIdOrderBySightingTimeDesc(UUID catId);
    
    Page<Sighting> findByReporterId(UUID reporterId, Pageable pageable);
    
    @Query("SELECT s FROM Sighting s WHERE s.cat.id = :catId AND s.sightingTime >= :startDate " +
           "ORDER BY s.sightingTime DESC")
    List<Sighting> findRecentSightingsByCatId(@Param("catId") UUID catId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT s FROM Sighting s WHERE s.sightingTime >= :startDate ORDER BY s.sightingTime DESC")
    List<Sighting> findRecentSightings(@Param("startDate") LocalDateTime startDate);
    
    // PostGIS 쿼리는 나중에 구현
    // @Query("SELECT s FROM Sighting s WHERE " +
    //        "ST_DWithin(s.coordinates, ST_GeogFromText(:point), :radiusInMeters) " +
    //        "AND s.sightingTime >= :startDate ORDER BY s.sightingTime DESC")
    // List<Sighting> findRecentSightingsWithinRadius(@Param("point") String point, 
    //                                                @Param("radiusInMeters") double radiusInMeters,
    //                                                @Param("startDate") LocalDateTime startDate);
    
    // PostGIS 쿼리는 나중에 구현
    // @Query("SELECT s FROM Sighting s WHERE s.coordinates && " +
    //        "ST_MakeEnvelope(:minLng, :minLat, :maxLng, :maxLat, 4326) " +
    //        "AND s.sightingTime >= :startDate ORDER BY s.sightingTime DESC")
    // List<Sighting> findRecentSightingsWithinBounds(@Param("minLng") double minLng, @Param("minLat") double minLat,
    //                                                @Param("maxLng") double maxLng, @Param("maxLat") double maxLat,
    //                                                @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT COUNT(s) FROM Sighting s WHERE s.sightingTime >= :startDate")
    Long countRecentSightings(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT s FROM Sighting s WHERE s.isVerified = false ORDER BY s.createdAt ASC")
    List<Sighting> findUnverifiedSightings();
}