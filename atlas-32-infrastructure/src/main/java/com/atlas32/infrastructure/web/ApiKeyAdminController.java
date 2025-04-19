package com.atlas32.infrastructure.web;

import com.atlas32.infrastructure.db.api.key.entity.ApiKeyEntity;
import com.atlas32.infrastructure.db.user.entity.UserEntity;
import com.atlas32.infrastructure.db.user.repository.UserRepository;
import com.atlas32.infrastructure.secret.ApiKeyService;
import java.security.Principal;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/api-keys")
public class ApiKeyAdminController {

  private final ApiKeyService apiKeyService;
  private final UserRepository userRepository;

  public ApiKeyAdminController(ApiKeyService apiKeyService,
      UserRepository userRepository) {
    this.apiKeyService = apiKeyService;
    this.userRepository = userRepository;
  }

  @GetMapping("/use")
  public String showUseForm() {
    return "api/key/api-key-use";
  }

  @PostMapping("/use")
  public String useApiKey(@RequestParam String passphrase,
      Principal principal,
      HttpSession session) {
    UserEntity user = userRepository.findByUsername(principal.getName())
        .orElseThrow(() -> new RuntimeException("User not found"));

    ApiKeyEntity apiKeyEntity = user.getApiKey();
    if (apiKeyEntity == null) {
      return "redirect:/admin/api-keys/use?error";
    }

    try {
      apiKeyService.retrieveApiKey(apiKeyEntity, passphrase);
    } catch (Exception e) {
      return "redirect:/admin/api-keys/use?error";
    }

    session.setAttribute("apiKeyPassphrase", passphrase);
    return "redirect:/device-map";
  }
}