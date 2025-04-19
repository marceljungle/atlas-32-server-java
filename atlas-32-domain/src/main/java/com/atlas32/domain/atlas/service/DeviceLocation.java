package com.atlas32.domain.atlas.service;

import com.atlas32.domain.atlas.device.Device;
import com.atlas32.domain.atlas.location.Location;

public interface DeviceLocation {

  void updateLocation(Device device, double lat, double lng);

  Location getLocation(String deviceId);

}
