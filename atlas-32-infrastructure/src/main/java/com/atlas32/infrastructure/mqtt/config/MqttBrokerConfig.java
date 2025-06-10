package com.atlas32.infrastructure.mqtt.config;

import java.util.UUID;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.ClientManager;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.core.Mqttv3ClientManager;

@Configuration
public class MqttBrokerConfig {

  @Value("${mqtt.broker.url}")
  private String brokerUrl;

  @Value("${mqtt.broker.username:#{null}}")
  private String username;

  @Value("${mqtt.broker.password:#{null}}")
  private String password;

  @Bean
  public String mqttClientId(@Value("${mqtt.client.id}") String baseClientId) {
    return baseClientId + "-" + UUID.randomUUID().toString();
  }

  @Bean
  public MqttConnectOptions mqttConnectOptions() {
    MqttConnectOptions options = new MqttConnectOptions();
    options.setServerURIs(new String[]{brokerUrl});
    if (username != null) {
      options.setUserName(username);
    }
    if (password != null) {
      options.setPassword(password.toCharArray());
    }
    options.setAutomaticReconnect(true);
    options.setCleanSession(false);
    options.setKeepAliveInterval(30);
    options.setConnectionTimeout(10);
    return options;
  }

  @Bean
  public MqttPahoClientFactory mqttClientFactory(MqttConnectOptions mqttConnectOptions) {
    DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
    factory.setConnectionOptions(mqttConnectOptions);
    return factory;
  }

  @Bean
  public ClientManager<IMqttAsyncClient, MqttConnectOptions> clientManager(
          MqttConnectOptions mqttConnectOptions, String mqttClientId) {
    return new Mqttv3ClientManager(mqttConnectOptions, mqttClientId);
  }

}