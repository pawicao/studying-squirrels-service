package pl.edu.agh.pawicao.studying_squirrels_api.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.OfferEditRequest;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.OfferRequest;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Subject;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Offer;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.OfferRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.PersonRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.SubjectRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OfferService {

  @Autowired
  private OfferRepository offerRepository;

  @Autowired
  private SubjectRepository subjectRepository;

  @Autowired
  private PersonRepository personRepository;

  public Offer createOffer(OfferRequest offerRequest) {
    Person tutor = personRepository.findById(offerRequest.getTutorId()).get();
    Subject subject = subjectRepository.findById(offerRequest.getSubjectId()).get();
    Offer offer = offerRepository.findBySubjectIdAndTutorId(offerRequest.getSubjectId(), offerRequest.getTutorId());
    if (offer == null) {
      offer = new Offer();
      offer.setSubject(subject);
      offer.setTutor(tutor);
    }
    offer.setActive(true);
    offer.setPrice(offerRequest.getPrice());
    Map<String, String> slotsAsString = offerRequest.getSlots().entrySet().stream().collect(Collectors.toMap(
      e -> e.getKey(),
      e -> String.join(",", e.getValue())
    ));
    offer.setTimeslots(slotsAsString);
    return offerRepository.save(offer);
  }

  public Offer editOffer(OfferEditRequest offerRequest) {
    Offer offer = offerRepository.findById(offerRequest.getOfferId()).get();
    offer.setPrice(offerRequest.getPrice());
    Map<String, String> slotsAsString = offerRequest.getSlots().entrySet().stream().collect(Collectors.toMap(
      e -> e.getKey(),
      e -> String.join(",", e.getValue())
    ));
    offer.setTimeslots(slotsAsString);
    return offerRepository.save(offer);
  }

  public List<Offer> getOffers(Long tutorId) {
    return offerRepository.findAllByTutorId(tutorId);
  }

  public Offer getOffer(Long offerId) {
    return offerRepository.findById(offerId).get();
  }

  public Long deleteOffer(Long offerId) {
    offerRepository.deleteOfferById(offerId);
    return offerId;
  }
}
