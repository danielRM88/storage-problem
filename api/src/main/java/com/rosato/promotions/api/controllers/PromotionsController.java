package com.rosato.promotions.api.controllers;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import javax.validation.Valid;

import com.rosato.promotions.api.models.FileUtil;
import com.rosato.promotions.api.models.UUIDGenerator;

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
  public static final String UPLOAD_FILE_NAME = "promotions-upload";

  @Autowired
  private UUIDGenerator uuidGenerator;
  @Autowired
  private FileUtil fileUtil;

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
  public String upload(@Valid @RequestBody UploadFileChunkRequest request) {
    boolean uploaded = false;
    String msg = "File chunk could not be uploaded";

    String filename = fileUtil.getUploadPath() + UPLOAD_FILE_NAME + ".part" + request.getChunkNumber();

    try (BufferedWriter w = Files.newBufferedWriter(Paths.get(filename))) {
      w.write(request.getChunkContent());
      uploaded = true;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // try (FileOutputStream out = new FileOutputStream(filename);
    // InputStream in = new
    // ByteArrayInputStream(request.getChunkContent().getBytes())) {
    // out.write(in.read());
    // uploaded = true;
    // } catch (FileNotFoundException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }

    if (uploaded) {
      msg = "File uploaded successfully";
    }

    return msg;
  }
}
