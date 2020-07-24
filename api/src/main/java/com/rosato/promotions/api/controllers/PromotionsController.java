package com.rosato.promotions.api.controllers;

import java.util.UUID;

import com.rosato.promotions.api.models.UUIDGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/promotions")
public class PromotionsController {
  @Autowired
  private UUIDGenerator uuidGenerator;

  class StartUploadResponse {
    private UUID uploadId;

    public StartUploadResponse(UUID id) {
      this.uploadId = id;
    }

    public UUID getUploadId() {
      return uploadId;
    }

    public void setUploadId(UUID uploadId) {
      this.uploadId = uploadId;
    }
  }

  @PostMapping("/start-upload")
  public StartUploadResponse startUpload() {
    return new StartUploadResponse(uuidGenerator.getUUID());
  }
}
