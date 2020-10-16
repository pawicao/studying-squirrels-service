package pl.edu.agh.pawicao.studying_squirrels_api.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

  @Autowired
  private PersonRepository personRepository;

  public List<Person> findAllByTutorIsTrue() { return personRepository.findAllByTutorIsTrue(); }

  public List<Person> findTutors(Long id, String city, Double rating, List<String> subjects, Double maxPrice) {
    return personRepository.findTutors(id, city, rating, subjects, maxPrice);
  }

  public List<Person> findNearTutors(Long id, Double rating, List<String> subjects, Double maxPrice) {
    return personRepository.findNearTutors(id, rating, subjects, maxPrice);
  }

}
