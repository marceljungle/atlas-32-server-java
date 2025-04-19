package com.atlas32.infrastructure.web;

import com.atlas32.infrastructure.db.api.key.entity.ApiKeyEntity;
import com.atlas32.infrastructure.db.user.entity.UserEntity;
import com.atlas32.infrastructure.db.user.repository.UserRepository;
import com.atlas32.infrastructure.secret.ApiKeyService;
import java.security.Principal;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

  private final UserRepository userRepository;

  private final ApiKeyService apiKeyService;

  public ViewController(UserRepository userRepository,
      ApiKeyService apiKeyService) {
    this.userRepository = userRepository;
    this.apiKeyService = apiKeyService;
  }

  @GetMapping("/device-map")
  public String deviceMap(Model model, Principal principal, HttpSession session) {
    String deviceId = "123";
    model.addAttribute("deviceId", deviceId);

    // Retrieve the user
    UserEntity user = userRepository.findByUsername(principal.getName())
        .orElseThrow(() -> new RuntimeException("User not found"));

    // Check if the user has an ApiKeyEntity
    ApiKeyEntity apiKey = user.getApiKey();
    if (apiKey == null) {
      model.addAttribute("googleMapsApiKey", "NO_KEY_CONFIGURED");
      return "device-map";
    }

    // Retrieve the passphrase from the session
    String passphrase = (String) session.getAttribute("apiKeyPassphrase");
    if (passphrase == null) {
      // No passphrase in the session => we cannot decrypt
      model.addAttribute("googleMapsApiKey", "NO_KEY_CONFIGURED");
      return "device-map";
    }

    try {
      String rawApiKey = apiKeyService.retrieveApiKey(apiKey, passphrase);
      model.addAttribute("googleMapsApiKey", rawApiKey);
    } catch (Exception e) {
      // If it fails (wrong passphrase, etc.)
      model.addAttribute("googleMapsApiKey", "INVALID_PASSPHRASE");
    }

    return "device-map";
  }
}