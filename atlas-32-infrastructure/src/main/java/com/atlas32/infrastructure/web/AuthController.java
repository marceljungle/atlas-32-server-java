package com.atlas32.infrastructure.web;

import com.atlas32.infrastructure.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

  private final UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/login")
  public String loginPage() {
    return "login/login";
  }

  @GetMapping("/register")
  public String registerForm(Model model) {
    return "login/register";
  }

  @PostMapping("/register")
  public String processRegister(@RequestParam String username,
      @RequestParam String password) {
    try {
      userService.registerUser(username, password);
      return "redirect:/login?registered";
    } catch (RuntimeException ex) {
      return "redirect:/register?error";
    }
  }
}