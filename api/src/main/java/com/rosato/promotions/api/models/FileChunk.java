package com.rosato.promotions.api.models;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

@Component
public class FileChunk implements Comparable<FileChunk> {
  private FileUtil fileUtil = new FileUtil();
  public static final String UPLOAD_FILE_NAME = "promotions-upload";

  private String filename;
  @NotNull
  @Min(1)
  private Integer chunkNumber;
  @NotNull
  private String content;

  public FileChunk() {
  }

  public FileChunk(String filename) {
    this.filename = filename;
    this.chunkNumber = Integer.parseInt(filename.split("-")[2]);
  }

  public String getContent() {
    return this.content;
  }

  @Override
  public int compareTo(FileChunk o) {
    return this.chunkNumber.compareTo(o.chunkNumber);
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getFilename() {
    return this.filename;
  }

  public Integer getChunkNumber() {
    return chunkNumber;
  }

  public void setChunkNumber(Integer chunkNumber) {
    this.chunkNumber = chunkNumber;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getUploadFilename() {
    // String savePath = System.getProperty("java.io.tmpdir")
    String result = getSavePath() + UPLOAD_FILE_NAME + ".part-" + this.chunkNumber;
    return result;
  }

  public String getSavePath() {
    // String savePath = System.getProperty("java.io.tmpdir")
    // String savePath = "./api/tmp/";

    return fileUtil.getUploadPath();
  }
}
