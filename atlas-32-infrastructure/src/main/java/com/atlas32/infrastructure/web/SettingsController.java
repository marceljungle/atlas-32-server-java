package com.atlas32.infrastructure.web;

import com.atlas32.infrastructure.db.api.key.entity.ApiKeyEntity;
import com.atlas32.infrastructure.db.user.entity.UserEntity;
import com.atlas32.infrastructure.db.user.repository.UserRepository;
import com.atlas32.infrastructure.secret.ApiKeyService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/settings")
public class SettingsController {

  private final UserRepository userRepository;

  private final ApiKeyService apiKeyService;

  public SettingsController(UserRepository userRepository,
      ApiKeyService apiKeyService) {
    this.userRepository = userRepository;
    this.apiKeyService = apiKeyService;
  }

  /**
  * Displays the settings screen, where the user can see if they already have a key and can
  * create/update it.
  */
  @GetMapping
  public String settings(Model model, Principal principal) {
    UserEntity user = userRepository.findByUsername(principal.getName())
        .orElseThrow(() -> new RuntimeException("User not found"));

    // We check if the user already has an associated ApiKey
    ApiKeyEntity keyEntity = user.getApiKey();
    if (keyEntity == null) {
      model.addAttribute("hasApiKey", false);
    } else {
      model.addAttribute("hasApiKey", true);
      model.addAttribute("apiKeyName", keyEntity.getName());
    }

    return "dashboard/settings/settings";
  }

  /**
   * Creates or updates the API key, assigning it to the user. Uses the passphrase to encrypt it.
   */
  @PostMapping("/key")
  public String updateApiKey(@RequestParam String name,
      @RequestParam String rawApiKey,
      @RequestParam String passphrase,
      Principal principal) {
    try {
      UserEntity user = userRepository.findByUsername(principal.getName())
          .orElseThrow(() -> new RuntimeException("User not found"));

      // We call createApiKeyForUser. This will overwrite
      // the user's previous key if it existed.
      apiKeyService.createApiKeyForUser(user.getId(), name, rawApiKey, passphrase);

      return "redirect:/settings?success";
    } catch (Exception e) {
      return "redirect:/settings?error";
    }
  }
}