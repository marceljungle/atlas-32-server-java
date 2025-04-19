package com.atlas32.infrastructure.bean.management;

import com.atlas32.domain.atlas.service.DeviceLocation;
import com.atlas32.domain.atlas.service.SendCommand;
import com.atlas32.usecase.atlas.ProcessLocationUpdate;
import com.atlas32.usecase.atlas.SendCommandToDevice;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanInitializer {

  @Bean
  public ProcessLocationUpdate commandFactory(DeviceLocation deviceLocationService) {
    return new ProcessLocationUpdate(deviceLocationService);
  }

  @Bean
  public SendCommandToDevice sendCommandToDevice(SendCommand sendCommand) {
    return new SendCommandToDevice(sendCommand);
  }
}
