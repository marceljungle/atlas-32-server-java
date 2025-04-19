package com.atlas32.usecase.atlas;

import com.atlas32.domain.atlas.device.Device;
import com.atlas32.domain.atlas.service.DeviceLocation;
import com.atlas32.usecase.atlas.params.LocationUpdateParams;

public class ProcessLocationUpdate {

  private final DeviceLocation deviceLocation;

  public ProcessLocationUpdate(DeviceLocation deviceLocation) {
    this.deviceLocation = deviceLocation;
  }

  public void execute(LocationUpdateParams params) {
    deviceLocation.updateLocation(new Device(params.getDeviceId()), params.getLat(),
        params.getLng());
  }
}