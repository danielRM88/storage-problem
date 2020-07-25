package com.rosato.promotions.api.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import com.rosato.promotions.api.models.FileChunk;
import com.rosato.promotions.api.models.FileUtil;
import com.rosato.promotions.api.models.Promotion;
import com.rosato.promotions.api.repositories.PromotionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PromotionServiceImpl implements PromotionService {
  @Autowired
  private PromotionRepository promotionRepository;
  @Autowired
  private FileUtil fileUtil;

  @Override
  public Promotion findById(Long id) {
    Promotion promotion = null;
    Optional<Promotion> result = promotionRepository.findById(id);
    if (result.isPresent()) {
      promotion = result.get();
    }
    return promotion;
  }

  @Override
  public List<Promotion> findAll() {
    return promotionRepository.findAll();
  }

  @Override
  public void create(Promotion promotion) {
    Promotion p = promotionRepository.save(promotion);
    System.out.println(p.getId());
  }

  @Override
  public boolean saveChunk(FileChunk chunk) {
    boolean saved = false;

    try (BufferedWriter w = Files.newBufferedWriter(Paths.get(chunk.getUploadFilename()))) {
      System.out.println(chunk.getContent());
      for (String line : chunk.getContent().split("\n")) {
        w.write(line);
        w.newLine();
      }
      saved = true;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return saved;
  }

  @Override
  public boolean buildPromotions() {
    boolean result = true;
    String regex = "promotions-upload.part-*";
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
        // StringBuilder fileUploadedContents = new StringBuilder();
        Path file = Paths.get(chunk.getFilename());
        try (Stream<String> fileStream = Files.lines(file)) {
          fileStream.forEach(line -> {
            try {
              w.write(line);
              w.newLine();
            } catch (IOException e) {
              e.printStackTrace();
            }
          });
        }
        file.toFile().delete();
      }
    } catch (IOException e) {
      e.printStackTrace();
      result = false;
    }

    populateTable(finalFilePath);
    finalFilePath.toFile().delete();

    return result;
  }

  private void populateTable(Path file) {
    try (InputStream inputFS = new FileInputStream(file.toFile());
        BufferedReader br = new BufferedReader(new InputStreamReader(inputFS))) {
      br.lines().forEach(line -> {
        String[] fields = line.split(",");
        Promotion p = new Promotion();
        p.setUuid(UUID.fromString(fields[0]));
        p.setPrice(Double.parseDouble(fields[1]));
        p.setExpirationDate(fields[2]);

        create(p);
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
