package com.rosato.promotions.api.repositories;

import com.rosato.promotions.api.models.Promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

}