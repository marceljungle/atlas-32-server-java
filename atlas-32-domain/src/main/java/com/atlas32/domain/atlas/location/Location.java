package com.atlas32.domain.atlas.location;

import com.atlas32.domain.atlas.device.Device;
import java.time.LocalDateTime;
import java.util.Objects;

public class Location {

  private final Device device;

  private final double latitude;

  private final double longitude;

  private final LocalDateTime timestamp;

  public Location(Device device, double latitude, double longitude, LocalDateTime timestamp) {
    this.device = device;
    this.latitude = latitude;
    this.longitude = longitude;
    this.timestamp = timestamp;
  }

  public Device getDevice() {
    return device;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Location location = (Location) o;
    return Double.compare(latitude, location.latitude) == 0
        && Double.compare(longitude, location.longitude) == 0 && Objects.equals(
        device, location.device) && Objects.equals(timestamp, location.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(device, latitude, longitude, timestamp);
  }

  @Override
  public String toString() {
    return "Location{"
        + "device=" + device
        + ",latitude=" + latitude
        + ",longitude=" + longitude
        + ",timestamp=" + timestamp
        + '}';
  }
}