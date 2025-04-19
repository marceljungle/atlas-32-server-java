package com.atlas32.infrastructure.rest;

import com.atlas32.domain.atlas.command.Command;
import com.atlas32.domain.atlas.command.CommandType;
import com.atlas32.domain.atlas.device.Device;
import com.atlas32.domain.atlas.location.Location;
import com.atlas32.infrastructure.location.DeviceLocationService;
import com.atlas32.infrastructure.mqtt.producer.CommandPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/device")
public class DeviceController {

  private final CommandPublisher commandPublisher;

  private final DeviceLocationService locationService;

  public DeviceController(CommandPublisher commandPublisher,
      DeviceLocationService locationService) {
    this.commandPublisher = commandPublisher;
    this.locationService = locationService;
  }

  /**
   * POST /api/device/{deviceId}/request-coords.
   */
  @PostMapping("/{deviceId}/request-coords")
  public void requestCoords(@PathVariable String deviceId) {
    commandPublisher.sendCommand(
        new Command(new Device(deviceId), CommandType.GET_COORDINATES, null));
  }

  /**
   * GET /api/device/{deviceId}/location.
   */
  @GetMapping("/{deviceId}/location")
  public Location getLocation(@PathVariable String deviceId) {
    return locationService.getLocation(deviceId);
  }
}