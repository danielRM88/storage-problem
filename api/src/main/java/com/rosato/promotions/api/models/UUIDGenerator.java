package com.rosato.promotions.api.models;

import java.util.UUID;

import org.springframework.stereotype.Component;

// This is a wrapper class to be able to
// mock it in the controllers tests.
// Mockito does not mock static methods
// Therefore this is necessary to test 
// the UUID method call
@Component
public class UUIDGenerator {
  public UUID getUUID() {
    return UUID.randomUUID();
  }
}
