package pl.edu.agh.pawicao.studying_squirrels_api.controller.api;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.BasicTutorDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Acquaintance;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.PersonRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.service.api.PersonService;
import pl.edu.agh.pawicao.studying_squirrels_api.util.exception.Mapper;

import java.util.List;


@RestController
@RequestMapping("/api")
public class PersonController {

  @Autowired
  private PersonService personService;

  @GetMapping("/helloTutors")
  ResponseEntity<List<BasicTutorDTO>> findPersonByTutorTrue() {
    return ResponseEntity.ok(Mapper.mapAll(personService.findAllByTutorIsTrue(), BasicTutorDTO.class));
  }

  @GetMapping("/tutors")
  ResponseEntity<List<BasicTutorDTO>> findTutors(
    @RequestParam Long id,
    @RequestParam(required = false) String city,
    @RequestParam(required = false) Double rating,
    @RequestParam(required = false) List<String> subjects,
    @RequestParam(required = false) Double maxPrice
  ) {
    return ResponseEntity.ok(
      Mapper.mapAll(personService.findTutors(id, city, rating, subjects, maxPrice), BasicTutorDTO.class)
    );
  }

  @GetMapping("/nearTutors")
  ResponseEntity<List<BasicTutorDTO>> findNearTutors(
    @RequestParam Long id,
    @RequestParam(required = false) Double rating,
    @RequestParam(required = false) List<String> subjects,
    @RequestParam(required = false) Double maxPrice
  ) {
    return ResponseEntity.ok(
      Mapper.mapAll(personService.findNearTutors(id, rating, subjects, maxPrice), BasicTutorDTO.class)
    );
  }

  @PostMapping("/friend")
  ResponseEntity<Acquaintance> createContactRequest(
    @RequestParam Long idOne, // TODO: Change to body params
    @RequestParam Long idTwo
  ) {
    return ResponseEntity.ok(
      personService.createContactRequest(idOne, idTwo)
    );
  }

}