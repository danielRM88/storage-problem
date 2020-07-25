package com.rosato.promotions.api.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.UUID;

import com.rosato.promotions.api.models.FileChunk;
import com.rosato.promotions.api.models.FileUtil;
import com.rosato.promotions.api.models.Promotion;
import com.rosato.promotions.api.services.PromotionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
public class PromotionsControllerTest {
  @Autowired
  private PromotionsController promotionsController;
  private FileUtil fileUtil;
  private PromotionService promotionService;

  @BeforeEach
  private void setup() {
    new File("./tmp").mkdirs();
    Path tests = Paths.get("./tmp/tests");
    try {
      Files.walk(tests).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    } catch (IOException e) {
      e.printStackTrace();
    }
    tests.toFile().mkdir();
  }

  @BeforeEach
  private void mockFileUtil() {
    fileUtil = mock(FileUtil.class);
  }

  @BeforeEach
  private void mockPromotionService() {
    promotionService = mock(PromotionService.class);
    ReflectionTestUtils.setField(promotionsController, "promotionService", promotionService);
  }

  @Test
  public void shouldCreateChunk() {
    String expected = "Test for upload chunk";
    FileChunk req = new FileChunk();
    ReflectionTestUtils.setField(req, "fileUtil", fileUtil);
    req.setChunkNumber(1);
    req.setContent(expected);

    when(promotionService.saveChunk(req)).thenReturn(true);

    String response = promotionsController.upload(req);
    assertEquals("File uploaded successfully", response);
    verify(promotionService).saveChunk(req);
  }

  @Test
  public void shouldReturnPromotion() {
    Promotion promotion = new Promotion();
    promotion.setUuid(UUID.randomUUID());
    promotion.setPrice(10.5d);
    promotion.setExpirationDate(LocalDateTime.now());

    Long id = 1L;
    when(promotionService.findById(id)).thenAnswer(new Answer<Promotion>() {
      public Promotion answer(InvocationOnMock invocation) throws Throwable {
        return promotion;
      }
    });

    Promotion response = promotionsController.getPromotion(id);
    assertEquals(promotion.getId(), response.getId());
  }

  @Test
  public void shouldBuildFileFromChunksAndPopulateDB() {
    promotionsController.finishUpload();
    verify(promotionService).buildPromotions();
  }

  @Test
  public void parsingTest() {
    String format = "2018-08-04 05:32:31 +0200 CEST";
    LocalDateTime dateTime = ZonedDateTime.parse(format, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss x z"))
        .toLocalDateTime();

    System.out.println(dateTime);
  }
}
