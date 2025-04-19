package com.atlas32.infrastructure.web;

import com.atlas32.infrastructure.db.api.key.entity.ApiKeyEntity;
import com.atlas32.infrastructure.db.device.repository.DeviceRepository;
import com.atlas32.infrastructure.db.user.entity.UserEntity;
import com.atlas32.infrastructure.db.user.repository.UserRepository;
import com.atlas32.infrastructure.secret.ApiKeyService;
import com.atlas32.infrastructure.web.dto.DeviceDto;
import java.security.Principal;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapController {

  private final UserRepository userRepo;

  private final DeviceRepository deviceRepo;

  private final ApiKeyService apiKeyService;

  public MapController(UserRepository userRepo,
      DeviceRepository deviceRepo,
      ApiKeyService apiKeyService) {
    this.userRepo = userRepo;
    this.deviceRepo = deviceRepo;
    this.apiKeyService = apiKeyService;
  }

  @GetMapping("/device-map")
  public String deviceMap(Model model,
      Principal principal,
      HttpSession session) throws Exception {

    UserEntity user = userRepo.findByUsername(principal.getName())
        .orElseThrow(() -> new RuntimeException("User not found"));

    // Load user’s devices
    model.addAttribute("devices",
        deviceRepo.findAllByOwnerId(user.getId())
            .stream()
            .map(d -> new DeviceDto(d.getName(), d.getMqttDeviceId()))
            .collect(Collectors.toList()));

    // Resolve Google Maps key
    ApiKeyEntity key = user.getApiKey();
    if (key == null) {
      model.addAttribute("googleMapsApiKey", "NO_KEY_CONFIGURED");
      return "map/device-map";
    }

    String pass = (String) session.getAttribute("apiKeyPassphrase");
    if (pass == null) {
      model.addAttribute("googleMapsApiKey", "NO_KEY_CONFIGURED");
      return "map/device-map";
    }

    try {
      model.addAttribute("googleMapsApiKey",
          apiKeyService.retrieveApiKey(key, pass));
    } catch (Exception e) {
      model.addAttribute("googleMapsApiKey", "INVALID_PASSPHRASE");
    }

    return "map/device-map";
  }
}