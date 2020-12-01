package pl.edu.agh.pawicao.studying_squirrels_api.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.ContactInfo;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.BasicPersonDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.RatingDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Acquaintance;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Offer;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.projection.Offer.OfferDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.AcquaintanceRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.PersonRepository;

import javax.security.auth.Subject;
import java.awt.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;

@Service
public class PersonService {

  @Autowired
  private PersonRepository personRepository;

  @Autowired
  private AcquaintanceRepository acquaintanceRepository;

  public List<Person> findAllByTutorIsTrue() {
    return personRepository.findAllByTutorIsTrue();
  }

  public List<Person> findTutors(Long id, String city, Double rating, List<String> subjects, Double maxPrice) {
    return personRepository.findTutors(id, city, rating, subjects, maxPrice);
  }

  public List<Person> findNearTutors(Long id, Double rating, List<String> subjects, Double maxPrice) {
    List<Person> nearTutors = personRepository.findNearTutorsWithPostalCode(id, rating, subjects, maxPrice);
    return nearTutors.isEmpty() ? personRepository.findNearTutors(id, rating, subjects, maxPrice) : nearTutors;
  }

  public Person findPerson(Long personId) {
    return personRepository.findById(personId).get();
  }

  public ContactInfo getContactStatus(Long idOne, Long idTwo) {
    Acquaintance acquaintance = acquaintanceRepository.getContactStatus(idOne, idTwo);
    if(acquaintance == null) return null;
    return new ContactInfo(
      acquaintance.getFriendOne().getId(),
      acquaintance.isAccepted()
    );
  }

  public List<RatingDTO> getRatings(Long personId, boolean student, Long subject) {
    return student ? personRepository.getStudentRatings(personId, subject) : personRepository.getTutorRatings(personId, subject);
  }

  public List<ZonedDateTime> getBusyTimeslots(Long tutorId, Long dateInMillis) {
    return personRepository.getBusyTimeslots(tutorId, dateInMillis);
  }

  public List<ZonedDateTime> getBusyTimeslots(Long tutorId) {
    return personRepository.getBusyTimeslots(tutorId);
  }

  public String addPhotoPath(Long personId, String photoPath) {
    return personRepository.setPhotoPath(personId, photoPath);
  }

  public String findFirstTimeslot(Person tutor) {
    List<ZonedDateTime> busyTimeslots = getBusyTimeslots(tutor.getId());
    String min = "Full";
    for(Offer offer : tutor.getOfferedSubjects()) {
      String firstTimeslotForOffer = findFirstTimeslotForOffer(offer, busyTimeslots);
      if(firstTimeslotForOffer.compareTo(min) < 0)
        min = firstTimeslotForOffer;
    }
    return min;
  }

  public Double findLowestPrice(Person tutor) {
    OptionalDouble optionalLowestPrice = tutor.getOfferedSubjects().stream()
      .filter(offer -> offer.getPrice() != null).mapToDouble(Offer::getPrice).min();
    return optionalLowestPrice.isPresent() ? optionalLowestPrice.getAsDouble() : null;
  }

  public String findFirstTimeslotForOffer(Offer offer, List<ZonedDateTime> busyTimeslots) {
    Map<String, List<String>> slots = OfferDTO.slotsAsList(offer.getTimeslots());
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime tmpDate = ZonedDateTime.now();
    while(true) {
      String dayOfWeek = String.valueOf(tmpDate.getDayOfWeek().getValue());
      if(!slots.containsKey(dayOfWeek)) {
        tmpDate = tmpDate.plusDays(1);
        continue;
      }
      for(String timeslot : slots.get(dayOfWeek)) {
        if (timeslot.isEmpty()) {
          continue;
        }
        ZonedDateTime resultTime = tmpDate
          .withHour(Integer.parseInt(timeslot.substring(0,2))).withMinute(0).withSecond(0).withNano(0);
        if(busyTimeslots.contains(resultTime) || resultTime.isBefore(now)) {
          tmpDate = tmpDate.plusDays(1);
          continue;
        }
        return resultTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      }
    }
  }

  public Person findRecommendedTutor(Long id, Double rating, List<String> subjects, Double maxPrice) {
    List<Person> recommendedTutors = personRepository
      .findRecommendedTutorsWithPostalCode(id, rating, subjects, maxPrice);
    if(!recommendedTutors.isEmpty())
      return recommendedTutors.get(0);
    recommendedTutors = personRepository.findRecommendedTutors(id, rating, subjects, maxPrice);
    return recommendedTutors.isEmpty() ? null : recommendedTutors.get(0);
  }
}
