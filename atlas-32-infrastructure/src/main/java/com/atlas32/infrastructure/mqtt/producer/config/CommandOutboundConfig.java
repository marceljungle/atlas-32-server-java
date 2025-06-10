package com.atlas32.infrastructure.mqtt.producer.config;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.ClientManager;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class CommandOutboundConfig {

  @Value("${mqtt.topic.command.publish}")
  private String mqttCommandPublishTopic;

  @Value("${mqtt.qos:1}")
  private int qos;

  @Bean
  public MessageChannel commandOutboundChannel() {
    return new DirectChannel();
  }

  @Bean
  @ServiceActivator(inputChannel = "commandOutboundChannel")
  public MessageHandler commandOutboundMessageHandler(
      ClientManager<IMqttAsyncClient, MqttConnectOptions> clientManager) {

    MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientManager);

    messageHandler.setAsync(true);
    messageHandler.setDefaultTopic(this.mqttCommandPublishTopic);
    messageHandler.setDefaultQos(qos);
    return messageHandler;
  }
}