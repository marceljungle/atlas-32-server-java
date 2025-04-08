package com.atlas32.domain.atlas.device;

import java.util.Objects;

public class Device {

  private final String identifier;

  public Device(String identifier) {
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return identifier;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Device device = (Device) o;
    return Objects.equals(identifier, device.identifier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(identifier);
  }

  @Override
  public String toString() {
    return "Device{"
        + "identifier=" + identifier
        + '}';
  }
}