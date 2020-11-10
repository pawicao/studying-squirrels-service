package pl.edu.agh.pawicao.studying_squirrels_api.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.IDPairRequest;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Acquaintance;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.AcquaintanceRepository;

import java.util.List;

@Service
public class AcquaintanceService {

  @Autowired
  private AcquaintanceRepository acquaintanceRepository;

  public Acquaintance createContactRequest(IDPairRequest ids) {
    return acquaintanceRepository.createContactRequest(ids.getIdOne(), ids.getIdTwo());
  }

  public Acquaintance acceptContactRequest(IDPairRequest ids) {
    return acquaintanceRepository.acceptContactRequest(ids.getIdOne(), ids.getIdTwo());
  }

  public Long deleteContact(IDPairRequest ids) {
    return acquaintanceRepository.deleteContact(ids.getIdOne(), ids.getIdTwo());
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
