package com.atlas32.infrastructure.web;

import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

  @GetMapping("/device-map")
  public String deviceMap(Model model, HttpSession session) {
    String deviceId = "123";
    model.addAttribute("deviceId", deviceId);

    String currentApiKey = (String) session.getAttribute("currentApiKey");

    if (currentApiKey == null) {
      currentApiKey = "NO_KEY_CONFIGURED";
    }

    model.addAttribute("googleMapsApiKey", currentApiKey);

    return "device-map";
  }
}