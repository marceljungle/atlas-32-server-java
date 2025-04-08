package com.atlas32.infrastructure.mqtt.producer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class CommandOutboundConfig {

  @Value("${mqtt.topic.command.publish}")
  private String mqttCommandPublishTopic;

  @Value("${mqtt.client.id}")
  private String baseClientId;

  @Value("${mqtt.qos:1}")
  private int qos;

  @Bean
  public MessageChannel commandOutboundChannel() {
    return new DirectChannel();
  }

  @Bean
  @ServiceActivator(inputChannel = "commandOutboundChannel")
  public MessageHandler commandOutboundMessageHandler(MqttPahoClientFactory mqttClientFactory) {
    MqttPahoMessageHandler messageHandler =
        new MqttPahoMessageHandler(
            baseClientId,
            mqttClientFactory);
    messageHandler.setAsync(true);
    messageHandler.setDefaultTopic(this.mqttCommandPublishTopic);
    messageHandler.setDefaultQos(qos);
    return messageHandler;
  }
}