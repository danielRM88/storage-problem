package com.rosato.promotions.api.controllers;

import javax.validation.Valid;

import com.rosato.promotions.api.models.FileChunk;
import com.rosato.promotions.api.models.Promotion;
import com.rosato.promotions.api.services.PromotionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/promotions")
public class PromotionsController {
  @Autowired
  private PromotionService promotionService;

  @PutMapping("/upload")
  @ResponseStatus(HttpStatus.OK)
  public String upload(@Valid @RequestBody FileChunk request) {
    String msg = "File chunk could not be uploaded";

    if (promotionService.saveChunk(request)) {
      msg = "File uploaded successfully";
    }

    return msg;
  }

  @PostMapping("/finish-upload")
  public String finishUpload() {
    String msg = "There was a problem finishing the upload";
    boolean result = promotionService.buildPromotions();
    return (result ? "Promotions created successfully" : msg);
  }

  @GetMapping("/{id}")
  public Promotion getPromotion(@PathVariable Long id) {
    return promotionService.findById(id);
  }
}
