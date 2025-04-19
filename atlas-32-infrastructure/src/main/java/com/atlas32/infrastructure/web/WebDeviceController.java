package com.atlas32.infrastructure.web;

import com.atlas32.infrastructure.db.device.entity.DeviceEntity;
import com.atlas32.infrastructure.db.device.repository.DeviceRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/devices")
public class WebDeviceController {

  private final DeviceRepository deviceRepository;

  public WebDeviceController(DeviceRepository deviceRepository) {
    this.deviceRepository = deviceRepository;
  }

  @GetMapping
  public String listDevices(Model model) {
    model.addAttribute("devices", deviceRepository.findAll());
    return "dashboard/device/devices";
  }

  @PostMapping("/add")
  public String addDevice(@RequestParam String name,
      @RequestParam String mqttDeviceId) {
    DeviceEntity device = DeviceEntity.builder()
        .name(name)
        .mqttDeviceId(mqttDeviceId)
        .build();
    deviceRepository.save(device);
    return "redirect:/devices";
  }

  @PostMapping("/{id}/delete")
  public String deleteDevice(@PathVariable Long id) {
    deviceRepository.deleteById(id);
    return "redirect:/devices";
  }
}