package com.atlas32.infrastructure.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

  @GetMapping("/")
  public String home() {
    return "redirect:/dashboard";
  }

  @GetMapping("/dashboard")
  public String dashboard() {
    return "dashboard/dashboard";
  }
}