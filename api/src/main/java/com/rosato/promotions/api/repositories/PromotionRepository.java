package com.rosato.promotions.api.repositories;

import com.rosato.promotions.api.models.Promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
  @Modifying
  @Transactional
  @Query(value = "TRUNCATE TABLE promotions;", nativeQuery = true)
  void truncateTable();
}
