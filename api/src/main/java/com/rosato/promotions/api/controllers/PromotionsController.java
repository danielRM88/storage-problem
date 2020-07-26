package com.rosato.promotions.api.controllers;

import java.util.HashMap;
import java.util.Map;

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

  @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Promotion not found")
  public static class PromotionNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
  }

  @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No file chunks have been uploaded")
  public static class NoFileChunksFound extends RuntimeException {
    private static final long serialVersionUID = 1L;
  }

  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Something went wrong creating file from uploaded chunks")
  public static class MergeChunksException extends RuntimeException {
    private static final long serialVersionUID = 1L;
  }

  @Autowired
  private PromotionService promotionService;

  @PutMapping("/upload")
  @ResponseStatus(HttpStatus.OK)
  public Map<String, String> upload(@Valid @RequestBody FileChunk request) {
    Map<String, String> response = new HashMap<>();
    String msg = "File chunk could not be uploaded";

    if (promotionService.saveChunk(request)) {
      msg = "File chunk uploaded successfully";
    }

    response.put("message", msg);

    return response;
  }

  @PostMapping("/finish-upload")
  public Map<String, String> finishUpload() {
    boolean result = promotionService.buildPromotions();

    if (!result) {
      throw new MergeChunksException();
    }

    Map<String, String> response = new HashMap<>();
    response.put("message", "Promotions are being created");

    return response;
  }

  @GetMapping("/{id}")
  public Promotion getPromotion(@PathVariable Long id) {
    Promotion promotion = promotionService.findById(id);
    if (promotion == null) {
      throw new PromotionNotFoundException();
    }
    return promotion;
  }
}
