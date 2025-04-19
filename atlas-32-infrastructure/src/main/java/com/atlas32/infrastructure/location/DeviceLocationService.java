package com.atlas32.infrastructure.location;

import com.atlas32.domain.atlas.device.Device;
import com.atlas32.domain.atlas.location.Location;
import com.atlas32.domain.atlas.service.DeviceLocation;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Service;

@Service
public class DeviceLocationService implements DeviceLocation {

  private final ConcurrentMap<String, Location> locationMap = new ConcurrentHashMap<>();

  public void updateLocation(Device device, double lat, double lng) {
    locationMap.put(device.getIdentifier(), new Location(device, lat, lng, LocalDateTime.now()));
  }

  public Location getLocation(String deviceId) {
    return locationMap.get(deviceId);
  }
}