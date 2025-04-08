package com.atlas32.infrastructure.web;

import com.atlas32.infrastructure.db.api.key.entity.ApiKeyEntity;
import com.atlas32.infrastructure.db.api.key.repository.ApiKeyRepository;
import com.atlas32.infrastructure.secret.ApiKeyService;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
@RequestMapping("/admin/api-keys")
public class ApiKeyAdminController {

  private final ApiKeyService apiKeyService;

  private final ApiKeyRepository apiKeyRepository;

  @GetMapping("/new")
  public String showNewApiKeyForm(Model model) {
    return "api/key/apiKey-new";
  }

  @PostMapping("/new")
  public String createApiKey(
      @RequestParam String name,
      @RequestParam String rawApiKey,
      @RequestParam String passphrase
  ) {
    try {
      apiKeyService.createApiKey(name, rawApiKey, passphrase);
      return "redirect:/admin/api-keys/list?success";
    } catch (Exception e) {
      return "redirect:/admin/api-keys/new?error";
    }
  }

  @GetMapping("/list")
  public String listApiKeys(Model model) {
    List<ApiKeyEntity> apiKeys = apiKeyRepository.findAll();
    model.addAttribute("apiKeys", apiKeys);
    return "api/key/apiKey-list";
  }

  @GetMapping("/{id}/use")
  public String showUseForm(@PathVariable("id") Long id, Model model) {
    model.addAttribute("apiKeyId", id);
    return "api/key/apiKey-use";
  }

  @PostMapping("/use")
  public String useApiKey(@RequestParam Long apiKeyId,
      @RequestParam String passphrase,
      HttpSession session) {
    try {
      String rawKey = apiKeyService.retrieveApiKey(apiKeyId, passphrase);
      session.setAttribute("currentApiKey", rawKey);
      return "redirect:/device-map";
    } catch (Exception e) {
      return "redirect:/admin/api-keys/" + apiKeyId + "/use?error";
    }
  }
}