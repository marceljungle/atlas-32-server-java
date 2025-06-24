package com.atlas32.infrastructure.rest;

import com.atlas32.domain.atlas.command.Command;
import com.atlas32.domain.atlas.command.CommandType;
import com.atlas32.domain.atlas.device.Device;
import com.atlas32.domain.atlas.location.Location;
import com.atlas32.infrastructure.location.DeviceLocationService;
import com.atlas32.infrastructure.rest.dto.CommandRequest;
import com.atlas32.usecase.atlas.SendCommandToDevice;
import com.atlas32.usecase.atlas.params.CommandToDeviceParams;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/device")
public class DeviceController {

  private final DeviceLocationService locationService;

  private final SendCommandToDevice sendCommandToDevice;

  public DeviceController(DeviceLocationService locationService,
      SendCommandToDevice sendCommandToDevice) {
    this.locationService = locationService;
    this.sendCommandToDevice = sendCommandToDevice;
  }

  /**
   * POST /api/device/{deviceId}/request-coords.
   */
  @PostMapping("/{deviceId}/command")
  public void commandRequest(@PathVariable String deviceId, @RequestBody CommandRequest request) {
    CommandToDeviceParams params = CommandToDeviceParams.builder()
        .withCommand(
            new Command(new Device(deviceId), CommandType.fromString(request.getCommand()), null))
        .build();
    sendCommandToDevice.execute(params);
  }

  /**
   * GET /api/device/{deviceId}/location.
   */
  @GetMapping("/{deviceId}/location")
  public Location getLocation(@PathVariable String deviceId) {
    return locationService.getLocation(deviceId);
  }
}