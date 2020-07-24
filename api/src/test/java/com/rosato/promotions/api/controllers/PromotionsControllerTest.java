package com.rosato.promotions.api.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;

import com.rosato.promotions.api.controllers.PromotionsController.StartUploadResponse;
import com.rosato.promotions.api.controllers.PromotionsController.UploadFileChunkRequest;
import com.rosato.promotions.api.models.FileUtil;
import com.rosato.promotions.api.models.UUIDGenerator;

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
  private UUIDGenerator uuidGenerator;
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
  private void mockUUID() {
    uuidGenerator = mock(UUIDGenerator.class);
    ReflectionTestUtils.setField(promotionsController, "uuidGenerator", uuidGenerator);
  }

  @BeforeEach
  private void mockFileUtil() {
    fileUtil = mock(FileUtil.class);
    ReflectionTestUtils.setField(promotionsController, "fileUtil", fileUtil);
  }

  @Test
  public void shouldReturnRandomUUID() {
    UUID uuid = UUID.randomUUID();
    when(uuidGenerator.getUUID()).thenAnswer(new Answer<UUID>() {
      public UUID answer(InvocationOnMock invocation) throws Throwable {
        return uuid;
      }
    });
    StartUploadResponse response = promotionsController.startUpload();
    assertEquals(uuid, response.getUploadId());
  }

  @Test
  public void shouldCreateChunk() {

    when(fileUtil.getUploadPath()).thenAnswer(new Answer<String>() {
      public String answer(InvocationOnMock invocation) throws Throwable {
        return "./tmp/tests/";
      }
    });

    String expected = "Test for upload chunk";
    UploadFileChunkRequest req = new UploadFileChunkRequest();
    req.setUploadId(uuidGenerator.getUUID());
    req.setChunkNumber(1);
    req.setChunkContent(expected);

    String response = promotionsController.upload(req);
    String filename = fileUtil.getUploadPath() + PromotionsController.UPLOAD_FILE_NAME + ".part" + req.getChunkNumber();
    System.out.println(filename);
    StringBuilder fileUploadedContents = new StringBuilder("");
    try (Stream<String> stream = Files.lines(Paths.get(filename))) {
      stream.forEach(fileUploadedContents::append);
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertEquals("File uploaded successfully", response);
    assertEquals(expected, fileUploadedContents.toString());
  }
}
