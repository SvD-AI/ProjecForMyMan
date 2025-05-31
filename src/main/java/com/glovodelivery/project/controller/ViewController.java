package com.glovodelivery.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

  @GetMapping("/register")
  public String showRegisterPage() {
    return "register"; // шукає templates/register.html
  }

  @GetMapping("/login")
  public String showLoginPage() {
    return "login"; // шукає templates/login.html
  }

  @GetMapping("/welcome")
  public String showWelcomePage() {
    return "welcome"; // шукає templates/login.html
  }

  @GetMapping("/admin-panel")
  public String getAdminPanelPage() {
    return "admin-dashboard";
  }


}
