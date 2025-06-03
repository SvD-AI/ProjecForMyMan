package com.glovodelivery.project.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class ViewController {

  @GetMapping("/register")
  public String showRegisterPage() {
    log.info("Accessed /register page");
    return "register"; 
  }

  @GetMapping("/login")
  public String showLoginPage() {
    log.info("Accessed /login page");
    return "login"; 
  }

  @GetMapping("/welcome")
  public String showWelcomePage() {
    log.info("Accessed /welcome page");
    return "welcome"; 
  }

  @GetMapping("/admin-panel")
  public String getAdminPanelPage() {
    log.info("Accessed /admin-panel page");
    return "admin-dashboard";
  }
}
