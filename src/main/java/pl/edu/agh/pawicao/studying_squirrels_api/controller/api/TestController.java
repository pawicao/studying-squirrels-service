package pl.edu.agh.pawicao.studying_squirrels_api.controller.api;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/test")
public class TestController {

  @GetMapping("/hello")
  String hello() {
    return "Hello World";
  }

  @GetMapping("/helloSecured")
  String helloSecured() {
    return "Secured Hello World";
  }

}