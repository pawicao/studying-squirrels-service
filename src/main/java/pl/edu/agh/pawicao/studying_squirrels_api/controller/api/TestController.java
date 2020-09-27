package pl.edu.agh.pawicao.studying_squirrels_api.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.BasicTutorDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.service.api.PersonService;
import pl.edu.agh.pawicao.studying_squirrels_api.util.exception.Mapper;

import java.util.List;


@RestController
@RequestMapping("/api")
public class TestController {

  // Controller

  @Autowired
  private PersonService personService;


  @GetMapping("/hello")
  String hello() {
    return "Hello World";
  }

  @GetMapping("/helloTutors")
  ResponseEntity<List<BasicTutorDTO>> findAllTutors() {
    return ResponseEntity.ok(Mapper.mapAll(personService.findAllTutors(), BasicTutorDTO.class));
  }

  @GetMapping("/helloTutors2")
  ResponseEntity<List<BasicTutorDTO>> findPersonByTutorTrue() {
    return ResponseEntity.ok(Mapper.mapAll(personService.findAllByTutorIsTrue(), BasicTutorDTO.class));
  }

  @GetMapping("/securedHello")
  String helloSecured() {
    return "Secured Hello World";
  }


}