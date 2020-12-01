package pl.edu.agh.pawicao.studying_squirrels_api.controller.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.ContactInfo;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.ContactInfoResponse;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.IDPairRequest;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.DetailedPersonAcceptDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.DetailedPersonDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.projection.Acquaintance.BasicAcquaintanceDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.BasicPersonAcquaintanceDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Acquaintance;
import pl.edu.agh.pawicao.studying_squirrels_api.service.api.AcquaintanceService;
import pl.edu.agh.pawicao.studying_squirrels_api.util.Mapper;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AcquaintanceController {

  private static BasicAcquaintanceDTO mapToDto(Acquaintance acquaintance) {
    BasicAcquaintanceDTO result = new BasicAcquaintanceDTO();
    result.setFriendsSince(acquaintance.getFriendsSince());
    result.setId(acquaintance.getId());
    result.setFriendOne(Mapper.map(acquaintance.getFriendOne(), BasicPersonAcquaintanceDTO.class));
    result.setFriendTwo(Mapper.map(acquaintance.getFriendTwo(), BasicPersonAcquaintanceDTO.class));
    return result;
  }

  @Autowired
  private AcquaintanceService acquaintanceService;

  @GetMapping("/friends")
  ResponseEntity<List<BasicPersonAcquaintanceDTO>> getContacts(
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
  ResponseEntity<ContactInfo> createContactRequest(
    @RequestBody IDPairRequest ids
  ) {
    Acquaintance acquaintance = acquaintanceService.createContactRequest(ids);
    return ResponseEntity.ok(new ContactInfo(acquaintance.getFriendOne().getId(), false));
  }

  @PutMapping("/friend")
  ResponseEntity<ContactInfoResponse> acceptContactRequest(
    @RequestBody IDPairRequest ids
  ) {
    Acquaintance acquaintance = acquaintanceService.acceptContactRequest(ids);
    return ResponseEntity.ok(
      new ContactInfoResponse(Mapper.map(
        acquaintance.getFriendOne(), DetailedPersonAcceptDTO.class),
        new ContactInfo(acquaintance.getFriendOne().getId(), true)
      ));
  }

  @DeleteMapping(value = "/friend/{idOne}")
  ResponseEntity<ContactInfo> deleteContact(
    @PathVariable Long idOne,
    @RequestParam(name = "id") Long idTwo
  ) {
    acquaintanceService.deleteContact(new IDPairRequest(idOne, idTwo));
    return ResponseEntity.ok(new ContactInfo());
  }
}
