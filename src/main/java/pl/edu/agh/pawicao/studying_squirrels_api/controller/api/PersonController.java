package pl.edu.agh.pawicao.studying_squirrels_api.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.ContactInfo;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.ContactInfoResponse;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.FileResponse;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.TutorWithTimeslotResponse;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.BasicTutorDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.DetailedPersonAcquaintanceDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.DetailedPersonDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.RatingDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Offer;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.projection.Offer.OfferDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.service.api.OfferService;
import pl.edu.agh.pawicao.studying_squirrels_api.service.api.PersonService;
import pl.edu.agh.pawicao.studying_squirrels_api.service.api.StorageService;
import pl.edu.agh.pawicao.studying_squirrels_api.util.DateUtils;
import pl.edu.agh.pawicao.studying_squirrels_api.util.FileUtils;
import pl.edu.agh.pawicao.studying_squirrels_api.util.Mapper;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class PersonController {

  @Autowired
  private PersonService personService;

  @Autowired
  private OfferService offerService;

  @Autowired
  private StorageService storageService;

  @GetMapping("/person/{personId}")
  ResponseEntity<ContactInfoResponse> findPerson(
    @PathVariable Long personId,
    @RequestParam(name = "id") Long myId
  ) {
    if (personId.equals(myId)) {
      return ResponseEntity.ok(new ContactInfoResponse(Mapper.map(personService.findPerson(personId), DetailedPersonAcquaintanceDTO.class), null));
    }
    ContactInfo contactInfo = personService.getContactStatus(myId, personId);
    if(contactInfo != null && contactInfo.isAccepted()) {
      return ResponseEntity.ok(new ContactInfoResponse(Mapper.map(personService.findPerson(personId), DetailedPersonAcquaintanceDTO.class), contactInfo));
    } else {
      return ResponseEntity.ok(new ContactInfoResponse(Mapper.map(personService.findPerson(personId), DetailedPersonDTO.class), contactInfo));
    }
  }

  @GetMapping("/person/{personId}/ratings")
  ResponseEntity<List <RatingDTO>> getRatings(
    @PathVariable Long personId,
    @RequestParam boolean student,
    @RequestParam(required = false) Long subject
  ) {
    return ResponseEntity.ok(personService.getRatings(personId, student, subject));
  }

  @GetMapping("/tutors/{tutorId}/timeslots")
  ResponseEntity<Map<String, List<String>>> getFreeTimeslots (
    @PathVariable Long tutorId,
    @RequestParam Long offerId,
    @RequestParam(name = "time") Long dateInMillis
  ) {
    Offer offer = offerService.getOffer(offerId);
    Map<String, List<String >> slots = OfferDTO.slotsAsList(offer.getTimeslots());
    List<ZonedDateTime> busyTimeslots = personService.getBusyTimeslots(tutorId, dateInMillis);
    Map<String, List<String>> result = new HashMap<>();
    ZonedDateTime now = DateUtils.millsToLocalDateTime(dateInMillis);
    for(int i = 0; i < 14; ++i) {
      ZonedDateTime tmpDate = DateUtils.millsToLocalDateTime(dateInMillis + i * 86400000);
      String dayOfWeek = String.valueOf(tmpDate.getDayOfWeek().getValue());
      if(!slots.containsKey(dayOfWeek))
        continue;
      List<String> resultHours = new ArrayList<>();
      for(String timeslot : slots.get(dayOfWeek)) {
        ZonedDateTime resultTime = tmpDate
          .withHour(Integer.parseInt(timeslot.substring(0,2))).withMinute(0).withSecond(0).withNano(0);
        if(busyTimeslots.contains(resultTime) || resultTime.isBefore(now))
          continue;
        resultHours.add(timeslot);
      }
      result.put(tmpDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), resultHours);
    }
    return ResponseEntity.ok(result);
  }

  @GetMapping("/helloTutors")
  ResponseEntity<List<BasicTutorDTO>> findPersonByTutorTrue() {
    return ResponseEntity.ok(Mapper.mapAll(personService.findAllByTutorIsTrue(), BasicTutorDTO.class));
  }

  @GetMapping("/tutors")
  ResponseEntity<List<TutorWithTimeslotResponse>> findTutors(
    @RequestParam Long id,
    @RequestParam(required = false) String city,
    @RequestParam(required = false) Double rating,
    @RequestParam(required = false) List<String> subjects,
    @RequestParam(required = false) Double maxPrice
  ) {
    List<Person> tutors = personService.findTutors(id, city, rating, subjects, maxPrice);
    return ResponseEntity
      .ok(tutors.stream()
        .map(tutor -> new TutorWithTimeslotResponse(Mapper.map(tutor, BasicTutorDTO.class),
          personService.findFirstTimeslot(tutor), personService.findLowestPrice(tutor), false))
        .collect(Collectors.toList()));
  }

  @GetMapping("/nearTutors")
  ResponseEntity<List<TutorWithTimeslotResponse>> findNearTutors(
    @RequestParam Long id,
    @RequestParam(required = false) Double rating,
    @RequestParam(required = false) List<String> subjects,
    @RequestParam(required = false) Double maxPrice
  ) {
    List<Person> tutors = personService.findNearTutors(id, rating, subjects, maxPrice);
    return ResponseEntity
      .ok(tutors.stream()
        .map(tutor -> new TutorWithTimeslotResponse(Mapper.map(tutor, BasicTutorDTO.class),
          personService.findFirstTimeslot(tutor), personService.findLowestPrice(tutor), false))
        .collect(Collectors.toList()));
  }

  @GetMapping("/recommendedTutor")
  ResponseEntity<TutorWithTimeslotResponse> findRecommendedTutor(
    @RequestParam Long id,
    @RequestParam(required = false) Double rating,
    @RequestParam(required = false) List<String> subjects,
    @RequestParam(required = false) Double maxPrice
  ) {
    Person recommendedTutor = personService.findRecommendedTutor(id, rating, subjects, maxPrice);
    return recommendedTutor == null ? ResponseEntity.notFound().build() :
      ResponseEntity.ok(new TutorWithTimeslotResponse(Mapper.map(recommendedTutor, BasicTutorDTO.class),
        personService.findFirstTimeslot(recommendedTutor), personService.findLowestPrice(recommendedTutor), true));
  }

  @RequestMapping(
    value = "/photos/{filename:.+}",
    method = RequestMethod.GET,
    produces = MediaType.IMAGE_JPEG_VALUE
  )
  @ResponseBody
  public ResponseEntity<byte[]> getPhoto(@PathVariable String filename) throws IOException {
    Resource resource = storageService.loadAsResource(filename);
    InputStream stream = resource.getInputStream();
    return ResponseEntity.ok(stream.readAllBytes());
  }

  @PostMapping("/photo")
  public ResponseEntity<FileResponse> uploadPhoto(
    @RequestParam("file") MultipartFile file,
    @RequestParam Long id
  ) throws IOException {
    String extension = Objects.requireNonNull(file.getOriginalFilename())
      .substring(file.getOriginalFilename().lastIndexOf("."));
    long randomNumber = new Date().getTime();
    MultipartFile newFile = FileUtils.getNewFile("person-" + id + '-' + randomNumber + extension, file);
    String name = storageService.store(newFile);
    String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
      .path("/photos/")
      .path(name)
      .toUriString();
    FileResponse response = new FileResponse(name, uri, file.getContentType(), file.getSize());
    String oldPhotopath = personService.addPhotoPath(id, "/photos/" + name);
    if (oldPhotopath != null) {
      storageService.delete(oldPhotopath.substring(8));
    }
    return ResponseEntity.ok(response);
  }

}