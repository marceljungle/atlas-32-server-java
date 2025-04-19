package com.atlas32.usecase.atlas;

import com.atlas32.domain.atlas.service.SendCommand;
import com.atlas32.usecase.atlas.params.CommandToDeviceParams;

public class SendCommandToDevice {

  private final SendCommand sendCommand;

  public SendCommandToDevice(SendCommand sendCommand) {
    this.sendCommand = sendCommand;
  }

  public void execute(CommandToDeviceParams params) {
    sendCommand.sendCommand(params.getCommand());
  }
}