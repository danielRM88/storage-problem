package com.rosato.promotions.api.controllers;

import java.util.UUID;

import javax.validation.Valid;

import com.rosato.promotions.api.models.FileChunk;
import com.rosato.promotions.api.models.UUIDGenerator;
import com.rosato.promotions.api.services.FileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
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
  private UUIDGenerator uuidGenerator;
  @Autowired
  private FileService fileService;

  public class StartUploadResponse {
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

  public static class UploadFileChunkRequest {
    @NonNull
    private UUID uploadId;
    @NonNull
    private Integer chunkNumber;
    @NonNull
    private String chunkContent;

    public UUID getUploadId() {
      return uploadId;
    }

    public void setUploadId(UUID uploadId) {
      this.uploadId = uploadId;
    }

    public Integer getChunkNumber() {
      return chunkNumber;
    }

    public void setChunkNumber(Integer chunkNumber) {
      this.chunkNumber = chunkNumber;
    }

    public String getChunkContent() {
      return chunkContent;
    }

    public void setChunkContent(String chunkContent) {
      this.chunkContent = chunkContent;
    }

  }

  @PostMapping("/start-upload")
  @ResponseStatus(HttpStatus.OK)
  public StartUploadResponse startUpload() {
    return new StartUploadResponse(uuidGenerator.getUUID());
  }

  @PutMapping("/upload")
  @ResponseStatus(HttpStatus.OK)
  public String upload(@Valid @RequestBody FileChunk request) {
    String msg = "File chunk could not be uploaded";

    if (fileService.save(request)) {
      msg = "File uploaded successfully";
    }

    return msg;
  }
}
