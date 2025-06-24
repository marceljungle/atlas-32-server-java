package com.atlas32.domain.atlas.command;

import java.util.Arrays;

public enum CommandType {
  PING,
  INIT_XTRA,
  SELECT_SATELLITES,
  LIVE_ON,
  LIVE_OFF,
  GET_COORDINATES,
  REST_REQUEST,
  UNKNOWN;

  public static CommandType fromString(String value) {
    return Arrays.stream(values())
        .filter(type -> type.name().equalsIgnoreCase(value))
        .findFirst()
        .orElse(UNKNOWN);
  }
}