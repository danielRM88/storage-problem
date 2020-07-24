package com.rosato.promotions.api.services;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.rosato.promotions.api.models.FileChunk;
import com.rosato.promotions.api.models.FileUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService {
  @Autowired
  private FileUtil fileUtil;

  public boolean save(FileChunk chunk) {
    boolean saved = false;

    try (BufferedWriter w = Files.newBufferedWriter(Paths.get(chunk.getUploadFilename()))) {
      w.write(chunk.getContent());
      saved = true;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return saved;
  }

  public Path build() {
    String regex = "promotions-upload.part*";
    String uploadString = fileUtil.getUploadPath();
    Path uploadPath = Paths.get(uploadString);
    Path finalFilePath = Paths.get(uploadString + "promotions-final.csv");
    System.out.println(finalFilePath.toString());

    List<FileChunk> chunks = new ArrayList<>();
    try (BufferedWriter w = Files.newBufferedWriter(finalFilePath);
        DirectoryStream<Path> dir = Files.newDirectoryStream(uploadPath, regex)) {
      dir.forEach(path -> {
        chunks.add(new FileChunk(path.toString()));
      });
      Collections.sort(chunks);

      for (FileChunk chunk : chunks) {
        System.out.println(chunk.getFilename());
        StringBuilder fileUploadedContents = new StringBuilder();
        try (Stream<String> fileStream = Files.lines(Paths.get(chunk.getFilename()))) {
          fileStream.forEach(fileUploadedContents::append);
          fileUploadedContents.append("\r\n");
          w.write(fileUploadedContents.toString());
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return finalFilePath;
  }
}
