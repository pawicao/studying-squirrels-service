package pl.edu.agh.pawicao.studying_squirrels_api.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.RatingDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.PersonRepository;

import javax.security.auth.Subject;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class PersonService {

  @Autowired
  private PersonRepository personRepository;

  public List<Person> findAllByTutorIsTrue() {
    return personRepository.findAllByTutorIsTrue();
  }

  public List<Person> findTutors(Long id, String city, Double rating, List<String> subjects, Double maxPrice) {
    return personRepository.findTutors(id, city, rating, subjects, maxPrice);
  }

  public List<Person> findNearTutors(Long id, Double rating, List<String> subjects, Double maxPrice) {
    return personRepository.findNearTutors(id, rating, subjects, maxPrice);
  }

  public Person findPerson(Long personId) {
    return personRepository.findById(personId).get();
  }

  public boolean areContacts(Long idOne, Long idTwo) {
    return personRepository.areContacts(idOne, idTwo);
  }

  public List<RatingDTO> getRatings(Long personId, boolean student) {
    return student ? personRepository.getStudentRatings(personId) : personRepository.getTutorRatings(personId);
  }

  public List<ZonedDateTime> getBusyTimeslots(Long tutorId, Long dateInMillis) {
    return personRepository.getBusyTimeslots(tutorId, dateInMillis);
  }

  public void addPhotoPath(Long personId, String photoPath) {
    personRepository.setPhotoPath(personId, photoPath);
  }
}
