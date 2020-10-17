package pl.edu.agh.pawicao.studying_squirrels_api.controller.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.BasicPersonAcquaintanceDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Acquaintance;
import pl.edu.agh.pawicao.studying_squirrels_api.service.api.AcquaintanceService;
import pl.edu.agh.pawicao.studying_squirrels_api.util.exception.Mapper;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AcquaintanceController {

  @Autowired
  private AcquaintanceService acquaintanceService;

  @GetMapping("/friends")
  ResponseEntity<List<BasicPersonAcquaintanceDTO>> createContactRequest(
    @RequestParam Long id,
    @RequestParam(required = false) boolean incoming,
    @RequestParam boolean accepted
  ) {
    List<Person> persons;
    if (accepted) {
      persons = acquaintanceService.findAllAcquaintances(id);
    } else {
      persons = incoming ? acquaintanceService.findReceivedAwaitingAcquaintances(id) :
        acquaintanceService.findSentAwaitingAcquaintances(id);
    }
    return ResponseEntity.ok(Mapper.mapAll(persons, BasicPersonAcquaintanceDTO.class));
  }

  @PostMapping("/friend")
  ResponseEntity<Acquaintance> createContactRequest(
    @RequestParam Long idOne, // TODO: Change to body params
    @RequestParam Long idTwo
  ) {
    return ResponseEntity.ok(
      acquaintanceService.createContactRequest(idOne, idTwo)
    );
  }

  @PutMapping("/friend")
  ResponseEntity<Acquaintance> acceptContactRequest(
    @RequestParam Long idOne, // TODO: Change to body params
    @RequestParam Long idTwo
  ) {
    return ResponseEntity.ok(
      acquaintanceService.acceptContactRequest(idOne, idTwo)
    );
  }

  @DeleteMapping("/friend")
  ResponseEntity<Long> deleteContact(
    @RequestParam Long idOne, // TODO: Change to body params
    @RequestParam Long idTwo
  ) {
    return ResponseEntity.ok(
      acquaintanceService.deleteContact(idOne, idTwo)
    );
  }
}
