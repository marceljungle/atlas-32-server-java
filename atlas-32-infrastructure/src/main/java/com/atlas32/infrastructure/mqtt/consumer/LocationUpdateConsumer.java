package com.atlas32.infrastructure.mqtt.consumer;

import com.atlas32.domain.atlas.device.Device;
import com.atlas32.infrastructure.location.DeviceLocationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class LocationUpdateConsumer {

  private final ObjectMapper objectMapper;

  private final DeviceLocationService deviceLocationService;

  @ServiceActivator(inputChannel = "locationUpdateChannel")
  public void handleLocationUpdate(Message<String> message) {
    String payload = message.getPayload();
    log.info("Location update message received (JSON): {}", payload);

    try {
      JsonNode rootNode = objectMapper.readTree(payload);
      String deviceId = rootNode.path("deviceId").asText();
      double lat = rootNode.path("latitude").asDouble();
      double lon = rootNode.path("longitude").asDouble();

      if (!deviceId.isEmpty() && !rootNode.path("latitude").isMissingNode() && !rootNode.path("longitude").isMissingNode()) {
        deviceLocationService.updateLocation(new Device(deviceId), lat, lon);
      } else {
        log.warn("Incomplete or missing fields in payload JSON: {}", payload);
      }
    } catch (IOException e) {
     log.error("Error parsing the payload JSON: {}", payload, e);
    }
  }
}