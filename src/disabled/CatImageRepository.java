package com.meowtown.repository;

import com.meowtown.entity.CatImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CatImageRepository extends JpaRepository<CatImage, UUID> {
    
    List<CatImage> findByCatIdOrderByImageOrder(UUID catId);
    
    @Query("SELECT ci FROM CatImage ci WHERE ci.cat.id = :catId ORDER BY ci.imageOrder ASC")
    List<CatImage> findImagesByCatId(@Param("catId") UUID catId);
    
    @Query("SELECT COUNT(ci) FROM CatImage ci WHERE ci.cat.id = :catId")
    Long countImagesByCatId(@Param("catId") UUID catId);
    
    void deleteByCatId(UUID catId);
    
    List<CatImage> findByUploadedById(UUID userId);
}