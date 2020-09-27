package pl.edu.agh.pawicao.studying_squirrels_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class CloudTempController {

  @GetMapping("/")
  String cloudHello() {
    return "Studying Squirrels Service";
  }

}
