package com.atlas32.infrastructure.mqtt.config;

import lombok.AllArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

@Configuration
public class MqttBrokerConfig {

  @Value("${mqtt.broker.url}")
  private String brokerUrl;

  @Value("${mqtt.broker.username:#{null}}")
  private String username;

  @Value("${mqtt.broker.password:#{null}}")
  private String password;

  @Bean
  public MqttPahoClientFactory mqttClientFactory() {
    final DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
    final MqttConnectOptions options = new MqttConnectOptions();
    options.setServerURIs(new String[]{brokerUrl});

    if (username != null) {
      options.setUserName(username);
    }
    if (password != null) {
      options.setPassword(password.toCharArray());
    }

    options.setCleanSession(true);
    // options.setConnectionTimeout(10);
    // options.setKeepAliveInterval(30);

    factory.setConnectionOptions(options);
    return factory;
  }

}