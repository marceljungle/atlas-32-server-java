package com.atlas32.infrastructure.mqtt.consumer.config;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.ClientManager;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

@Configuration
public class LocationUpdateConfig {

  @Value("${mqtt.topic.response.subscribe}")
  private String mqttLocationSubscribeTopic;

  @Value("${mqtt.client.id}")
  private String baseClientId;

  @Value("${mqtt.qos:1}")
  private int qos;

  @Bean
  public MessageChannel locationUpdateChannel() {
    return new DirectChannel();
  }

  /**
   * Message producer for the 'location updates' topic. Here we define the
   * MqttPahoMessageDrivenChannelAdapter that "listens" to the MQTT broker for the specified topic.
   */
  @Bean
  public MessageProducer locationUpdateInbound(
      ClientManager<IMqttAsyncClient, MqttConnectOptions> clientManager) {

    MqttPahoMessageDrivenChannelAdapter adapter =
        new MqttPahoMessageDrivenChannelAdapter(clientManager, this.mqttLocationSubscribeTopic);

    adapter.setConverter(new DefaultPahoMessageConverter());
    adapter.setQos(qos);
    adapter.setOutputChannel(locationUpdateChannel());
    return adapter;
  }
}