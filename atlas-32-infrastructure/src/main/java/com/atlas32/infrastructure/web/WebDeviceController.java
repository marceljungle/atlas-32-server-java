package com.atlas32.infrastructure.web;

import com.atlas32.infrastructure.db.device.entity.DeviceEntity;
import com.atlas32.infrastructure.db.device.repository.DeviceRepository;
import com.atlas32.infrastructure.db.user.entity.UserEntity;
import com.atlas32.infrastructure.db.user.repository.UserRepository;
import java.security.Principal;
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

  private final DeviceRepository deviceRepo;

  private final UserRepository userRepo;

  public WebDeviceController(DeviceRepository deviceRepo, UserRepository userRepo) {
    this.deviceRepo = deviceRepo;
    this.userRepo = userRepo;
  }

  @GetMapping
  public String listDevices(Model model, Principal principal) {
    UserEntity user = userRepo.findByUsername(principal.getName())
        .orElseThrow(() -> new RuntimeException("User not found"));
    model.addAttribute("devices", deviceRepo.findAllByOwnerId(user.getId()));
    return "dashboard/device/devices";
  }

  @PostMapping("/add")
  public String addDevice(@RequestParam String name,
      @RequestParam String mqttDeviceId,
      Principal principal) {

    UserEntity user = userRepo.findByUsername(principal.getName())
        .orElseThrow(() -> new RuntimeException("User not found"));

    DeviceEntity device = DeviceEntity.builder()
        .name(name)
        .mqttDeviceId(mqttDeviceId)
        .owner(user)
        .build();
    deviceRepo.save(device);
    return "redirect:/devices";
  }

  @PostMapping("/{id}/delete")
  public String deleteDevice(@PathVariable Long id, Principal principal) {

    // Optional: check ownership before deleting
    deviceRepo.deleteById(id);
    return "redirect:/devices";
  }
}