package com.rosato.promotions.api.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import com.rosato.promotions.api.controllers.PromotionsController.StartUploadResponse;
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

  @BeforeEach
  private void mockUUID() {
    uuidGenerator = mock(UUIDGenerator.class);
    ReflectionTestUtils.setField(promotionsController, "uuidGenerator", uuidGenerator);
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
}
