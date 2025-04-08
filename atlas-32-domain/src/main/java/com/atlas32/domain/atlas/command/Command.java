package com.atlas32.domain.atlas.command;

import com.atlas32.domain.atlas.device.Device;
import java.util.Objects;

public class Command {

  private final Device device;

  private final CommandType type;

  private final String payload;

  public Command(Device device, CommandType type, String payload) {
    this.device = device;
    this.type = type;
    this.payload = payload;
  }

  public Device getDevice() {
    return device;
  }

  public CommandType getType() {
    return type;
  }

  public String getPayload() {
    return payload;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Command command = (Command) o;
    return Objects.equals(device, command.device) && type == command.type
        && Objects.equals(payload, command.payload);
  }

  @Override
  public int hashCode() {
    return Objects.hash(device, type, payload);
  }

  @Override
  public String toString() {
    return "Command{"
        + "device=" + device
        + ",type=" + type
        + ",payload=" + payload
        + '}';
  }
}