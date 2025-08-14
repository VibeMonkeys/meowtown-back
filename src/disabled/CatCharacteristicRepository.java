package com.meowtown.repository;

import com.meowtown.entity.CatCharacteristic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CatCharacteristicRepository extends JpaRepository<CatCharacteristic, UUID> {
    
    List<CatCharacteristic> findByCatId(UUID catId);
    
    @Query("SELECT cc FROM CatCharacteristic cc WHERE cc.cat.id = :catId AND cc.characteristic = :characteristic")
    CatCharacteristic findByCatIdAndCharacteristic(@Param("catId") UUID catId, @Param("characteristic") String characteristic);
    
    @Query("SELECT DISTINCT cc.characteristic FROM CatCharacteristic cc")
    List<String> findAllUniqueCharacteristics();
    
    @Query("SELECT cc.characteristic, COUNT(cc) FROM CatCharacteristic cc GROUP BY cc.characteristic ORDER BY COUNT(cc) DESC")
    List<Object[]> findCharacteristicCounts();
    
    void deleteByCatId(UUID catId);
    
    void deleteByCat(com.meowtown.entity.Cat cat);
    
    void deleteByCatIdAndCharacteristic(UUID catId, String characteristic);
}