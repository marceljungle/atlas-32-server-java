package com.atlas32.usecase.atlas.params;

import java.util.Objects;

public class LocationUpdateParams {

  private final String deviceId;

  private final double lat;

  private final double lng;

  private LocationUpdateParams(Builder builder) {
    deviceId = builder.deviceId;
    lat = builder.lat;
    lng = builder.lng;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public double getLat() {
    return lat;
  }

  public double getLng() {
    return lng;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LocationUpdateParams that = (LocationUpdateParams) o;
    return Double.compare(lat, that.lat) == 0
        && Double.compare(lng, that.lng) == 0 && Objects.equals(deviceId,
        that.deviceId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(deviceId, lat, lng);
  }

  @Override
  public String toString() {
    return "LocationUpdateParams{"
        + "deviceId=" + deviceId
        + ",lat=" + lat
        + ",lng=" + lng
        + '}';
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private String deviceId;
    private double lat;
    private double lng;

    private Builder() {
    }

    public Builder withDeviceId(String deviceId) {
      this.deviceId = deviceId;
      return this;
    }

    public Builder withLat(double lat) {
      this.lat = lat;
      return this;
    }

    public Builder withLng(double lng) {
      this.lng = lng;
      return this;
    }

    public LocationUpdateParams build() {
      return new LocationUpdateParams(this);
    }
  }
}
