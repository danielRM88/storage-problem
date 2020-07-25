package com.rosato.promotions.api.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import com.rosato.promotions.api.models.FileChunk;
import com.rosato.promotions.api.models.FileUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
public class PromotionServiceImplTest {
  @Autowired
  private PromotionService promotionService;
  private FileUtil fileUtil;

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
    ReflectionTestUtils.setField(promotionService, "fileUtil", fileUtil);
  }

  @Test
  public void shouldSaveFileChunk() {
    when(fileUtil.getUploadPath()).thenAnswer(new Answer<String>() {
      public String answer(InvocationOnMock invocation) throws Throwable {
        return "./tmp/tests/";
      }
    });

    String expected = "Test for upload chunk";
    FileChunk req = new FileChunk();
    ReflectionTestUtils.setField(req, "fileUtil", fileUtil);
    req.setChunkNumber(1);
    req.setContent(expected);

    boolean response = promotionService.saveChunk(req);
    StringBuilder fileUploadedContents = new StringBuilder("");
    try (Stream<String> stream = Files.lines(Paths.get(req.getUploadFilename()))) {
      stream.forEach(fileUploadedContents::append);
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertEquals(true, response);
    assertEquals(expected, fileUploadedContents.toString());
  }

  @Test
  public void shouldBuildFinalFileFromChunks() {
    when(fileUtil.getUploadPath()).thenAnswer(new Answer<String>() {
      public String answer(InvocationOnMock invocation) throws Throwable {
        return "./tmp/tests/";
      }
    });

    StringBuilder builder = new StringBuilder();
    Random rand = new Random();
    for (int i = 1; i < 6; i++) {
      String expected = UUID.randomUUID() + "," + rand.nextDouble() + ",2018-08-04 05:32:31 +0200 CEST";
      builder.append(expected);
      FileChunk req = new FileChunk();
      ReflectionTestUtils.setField(req, "fileUtil", fileUtil);
      req.setChunkNumber(i);
      req.setContent(expected);

      boolean response = promotionService.saveChunk(req);
      assertEquals(true, response);
    }

    promotionService.buildPromotions();
    assertEquals(5, promotionService.findAll().size());
  }
}
