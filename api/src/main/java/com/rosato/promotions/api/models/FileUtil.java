package com.rosato.promotions.api.models;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileUtil {
  @Autowired
  ServletContext context;

  public String getUploadPath() {
    return "./api/tmp/";
  }
}
