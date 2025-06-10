package com.atlas32.infrastructure.mqtt.producer;

import com.atlas32.domain.atlas.command.Command;
import com.atlas32.domain.atlas.service.SendCommand;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CommandPublisher implements SendCommand {

  private final MessageChannel commandOutboundChannel;

  private final ObjectMapper objectMapper;

  public CommandPublisher(
      @Qualifier("commandOutboundChannel") MessageChannel commandOutboundChannel,
      ObjectMapper objectMapper) {
    this.commandOutboundChannel = commandOutboundChannel;
    this.objectMapper = objectMapper;
  }

  @Override
  public void sendCommand(Command command) {
    String topic = String.format("device/%s/command", command.getDevice().getIdentifier());
    String payload;

    Map<String, String> commandMap = new HashMap<>();
    commandMap.put("command", command.getType().toString());

    try {
      payload = objectMapper.writeValueAsString(commandMap);
    } catch (JsonProcessingException e) {
      log.error("Error converting command to JSON: {}", command, e);
      return;
    }

    org.springframework.messaging.Message<String> message = MessageBuilder
        .withPayload(payload)
        .setHeader(MqttHeaders.TOPIC, topic)
        .build();

    log.info("Sending command to topic: [{}], payload: [{}]", topic, payload);
    commandOutboundChannel.send(message);
  }
}