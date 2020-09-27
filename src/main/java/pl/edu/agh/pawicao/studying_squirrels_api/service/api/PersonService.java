package pl.edu.agh.pawicao.studying_squirrels_api.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.BasicTutorProjection;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.PersonRepository;

import java.util.List;

@Service
public class PersonService {

  @Autowired
  private PersonRepository personRepository;

  public List<Person> findAllTutors() {
    return personRepository.findAllTutors();
  }

  public List<Person> findAllByTutorIsTrue() { return personRepository.findAllByTutorIsTrue(); }

}
