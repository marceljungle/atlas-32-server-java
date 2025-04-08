package com.atlas32.infrastructure.bean.management;

import com.atlas32.usecase.atlas.ReceiveLocationUpdate;
import com.atlas32.usecase.atlas.SendCommandToDevice;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanInitializer {

  @Bean
  public ReceiveLocationUpdate commandFactory() {
    return new ReceiveLocationUpdate();
  }

  @Bean
  public SendCommandToDevice sendCommandToDevice() {
    return new SendCommandToDevice();
  }
}
