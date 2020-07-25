package com.rosato.promotions.api.models;

import org.springframework.stereotype.Component;

@Component
public class FileUtil {
  public String getUploadPath() {
    return System.getProperty("java.io.tmpdir") + "/";
  }
}
