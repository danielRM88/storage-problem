package com.rosato.promotions.api.services;

import com.rosato.promotions.api.models.FileChunk;
import com.rosato.promotions.api.models.Promotion;

import org.springframework.stereotype.Service;

@Service
public interface PromotionService {
  Promotion findById(Long id);

  void create(Promotion promotion);

  boolean saveChunk(FileChunk chunk);

  boolean buildPromotions();
}
