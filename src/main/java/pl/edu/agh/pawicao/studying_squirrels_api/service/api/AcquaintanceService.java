package pl.edu.agh.pawicao.studying_squirrels_api.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Acquaintance;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.AcquaintanceRepository;

import java.util.List;

@Service
public class AcquaintanceService {

  @Autowired
  private AcquaintanceRepository acquaintanceRepository;

  public Acquaintance createContactRequest(Long idOne, Long idTwo) {
    return acquaintanceRepository.createContactRequest(idOne, idTwo);
  }

  public Acquaintance acceptContactRequest(Long idOne, Long idTwo) {
    return acquaintanceRepository.acceptContactRequest(idOne, idTwo);
  }

  public Long deleteContact(Long idOne, Long idTwo) {
    return acquaintanceRepository.deleteContact(idOne, idTwo);
  }

  public List<Person> findAllAcquaintances(Long id) {
    return acquaintanceRepository.findAllAcquaintances(id);
  }

  public List<Person> findReceivedAwaitingAcquaintances(Long id) {
    return acquaintanceRepository.findReceivedAwaitingAcquaintances(id);
  }

  public List<Person> findSentAwaitingAcquaintances(Long id) {
    return acquaintanceRepository.findSentAwaitingAcquaintances(id);
  }
}
