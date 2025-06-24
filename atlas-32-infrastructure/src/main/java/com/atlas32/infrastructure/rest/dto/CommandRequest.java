package com.atlas32.infrastructure.rest.dto;

import lombok.Data;

@Data
public class CommandRequest {

  private final String command;

  public String getCommand() {
    return command;
  }
}