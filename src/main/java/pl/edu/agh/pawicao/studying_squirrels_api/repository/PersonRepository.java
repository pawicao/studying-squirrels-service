package pl.edu.agh.pawicao.studying_squirrels_api.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.RolesResponse;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.PersonCredentialsProjection;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.RatingDTO;

import java.time.ZonedDateTime;
import java.util.List;

public interface PersonRepository extends Neo4jRepository<Person, Long> {

  boolean existsByEmail(String email);

  PersonCredentialsProjection findPersonByEmail(String email);

  List<Person> findAllByTutorIsTrue();

  @Query(
    "MATCH (n:Person) WHERE ID(n) = $id RETURN n"
  )
  Person getRoles(Long id);

  @Query("MATCH (n:Person) WHERE ID(n) = $id SET n.student = true RETURN true")
  boolean makeStudent(Long id);

  @Query("MATCH (n:Person) WHERE ID(n) = $id SET n.tutor = true RETURN true")
  boolean makeTutor(Long id);

  @Query(
    "CREATE (n:Person {email: $email, password: $password, firstName: $firstName, " +
    "lastName: $lastName, phone: $phone, photoPath: null, student: $student, " +
    "tutor: $tutor, dateOfBirth: datetime({timezone: 'Europe/Warsaw', epochSeconds: $dateInMillis/1000})}) " +
    "RETURN n"
  )
  Person addPerson(
    String email, String password, String firstName, String lastName, String phone,
    boolean student, boolean tutor, Long dateInMillis
  );

  @Query(
    "MATCH (sub:Subject)<-[offer:OFFERS {active: true}]-(tutor:Person)-[tutorPlace:LIVES_IN]->(tutorCity:City), (student:Person) " +
    "WHERE tutor.tutor = true AND ID(student) = $id AND ID(tutor) <> $id " +
    "AND ($cityName is null OR tutorCity.name = $cityName) " +
    "AND ($rating is null OR tutor.tutorRating >= $rating) " +
    "AND ($subjects is null OR ANY(subject in $subjects WHERE sub.name = subject)) " +
    "AND ($maxPrice is null OR offer.price <= $maxPrice) " +
    "RETURN tutor as person, tutorPlace as placeOfResidence, tutorCity as city, offer as offeredSubject, sub as subject"
  )
  List<Person> findTutors(Long id, String cityName, Double rating, List<String> subjects, Double maxPrice);

  @Query(
    "MATCH (sub:Subject)<-[offer:OFFERS {active: true}]-(tutor:Person)-[tutorPlace:LIVES_IN]->" +
    "(city:City)<-[studentPlace:LIVES_IN]-(student:Person) " +
    "WHERE tutor.tutor = true AND ID(student) = $id AND ID(tutor) <> $id " +
    "AND tutorPlace.postalCode = studentPlace.postalCode " +
    "AND ($rating is null OR tutor.tutorRating >= $rating) " +
    "AND ($subjects is null OR ANY(subject in $subjects WHERE sub.name = subject)) " +
    "AND ($maxPrice is null OR offer.price <= $maxPrice) " +
    "RETURN tutor as person, tutorPlace as placeOfResidence, city, offer as offeredSubject, sub as subject"
  )
  List<Person> findNearTutorsWithPostalCode(Long id, Double rating, List<String> subjects, Double maxPrice);

  @Query(
    "MATCH (sub:Subject)<-[offer:OFFERS {active: true}]-(tutor:Person)-[tutorPlace:LIVES_IN]->" +
    "(city:City)<-[studentPlace:LIVES_IN]-(student:Person) " +
    "MATCH (tutor)-[gave:GAVE]->(:Lesson)<-[:TOOK]-(:Person)-[:IS_FRIEND {accepted: true}]-(student) " +
    "WITH gave, sub, offer, tutor, tutorPlace, city, studentPlace, student, " +
    "AVG(gave.tutorRating) as averageTutorRating " +
    "WHERE tutor.tutor = true AND ID(student) = $id AND ID(tutor) <> $id " +
    "AND tutorPlace.postalCode = studentPlace.postalCode " +
    "AND ($rating is null OR tutor.tutorRating >= $rating) " +
    "AND ($subjects is null OR ANY(subject in $subjects WHERE sub.name = subject)) " +
    "AND ($maxPrice is null OR offer.price <= $maxPrice) " +
    "AND averageTutorRating > 4.0 " +
    "RETURN tutor as person, tutorPlace as placeOfResidence, city, offer as offeredSubject, sub as subject " +
    "ORDER BY averageTutorRating DESC"
  )
  List<Person> findRecommendedTutorsWithPostalCode(Long id, Double rating, List<String> subjects, Double maxPrice);

  @Query(
    "MATCH (sub:Subject)<-[offer:OFFERS {active: true}]-(tutor:Person)-[tutorPlace:LIVES_IN]->" +
    "(city:City)<-[studentPlace:LIVES_IN]-(student:Person) " +
    "WHERE tutor.tutor = true AND ID(student) = $id AND ID(tutor) <> $id " +
    "AND ($rating is null OR tutor.tutorRating >= $rating) " +
    "AND ($subjects is null OR ANY(subject in $subjects WHERE sub.name = subject)) " +
    "AND ($maxPrice is null OR offer.price <= $maxPrice) " +
    "RETURN tutor as person, tutorPlace as placeOfResidence, city, offer as offeredSubject, sub as subject"
  )
  List<Person> findNearTutors(Long id, Double rating, List<String> subjects, Double maxPrice);

  @Query(
    "MATCH (tutor:Person)-[gave:GAVE]->(l:Lesson)<-[t:TOOK]-(:Person)-[:IS_FRIEND {accepted: true}]-(student:Person) " +
    "WITH gave, tutor, student, " +
    "COLLECT(gave.tutorRating) as averageTutorRatingList " +
    "MATCH (sub:Subject)<-[offer:OFFERS {active: true}]-(tutor)-[tutorPlace:LIVES_IN]->" +
    "(city:City)<-[studentPlace:LIVES_IN]-(student) " +
    "UNWIND averageTutorRatingList as values " +
    "WITH AVG(values) as averageTutorRating, tutor, student, sub, offer, tutorPlace, city " +
    "WHERE tutor.tutor = true AND ID(student) = $id AND ID(tutor) <> $id " +
    "AND ($rating is null OR tutor.tutorRating >= $rating) " +
    "AND ($subjects is null OR ANY(subject in $subjects WHERE sub.name = subject)) " +
    "AND ($maxPrice is null OR offer.price <= $maxPrice) " +
    "AND averageTutorRating > 4.0 " +
    "RETURN tutor as person, tutorPlace as placeOfResidence, city, offer as offeredSubject, sub as subject " +
    "ORDER BY averageTutorRating DESC"
  )
  List<Person> findRecommendedTutors(Long id, Double rating, List<String> subjects, Double maxPrice);

  @Query(
    "MATCH (p1:Person), (p2:Person) WHERE ID(p1) = $idOne AND ID(p2) = $idTwo " +
    "RETURN EXISTS((p1)-[:IS_FRIEND {accepted: true}]-(p2))"
  )
  boolean areContacts(Long idOne, Long idTwo);

  @Query(
    "MATCH (n:Person)-[took:TOOK]->(lesson:Lesson)-[isOf:IS_OF]->(subject:Subject) " +
    "MATCH (issuer:Person)-[:GAVE]->(lesson) " +
    "WHERE ID(n) = $personId and EXISTS(took.studentRating) " +
    "AND ($subjectId is null OR ID(subject) = $subjectId) " +
    "RETURN took.tutorRating as rating, took.tutorRatingDescription AS ratingDescription, " +
    "lesson.date AS date, subject.name AS subject, subject.icon AS subjectIcon, issuer.firstName AS issuerName"
  )
  List<RatingDTO> getStudentRatings(Long personId, Long subjectId);

  @Query(
    "MATCH (n:Person)-[gave:GAVE]->(lesson:Lesson)-[isOf:IS_OF]->(subject:Subject) " +
    "MATCH (issuer:Person)-[:TOOK]->(lesson) " +
    "WHERE ID(n) = $personId and EXISTS(gave.tutorRating) " +
    "AND ($subjectId is null OR ID(subject) = $subjectId) " +
    "RETURN gave.tutorRating as rating, gave.tutorRatingDescription AS ratingDescription, " +
    "lesson.date AS date, subject.name AS subject, subject.icon AS subjectIcon, issuer.firstName AS issuerName"
  )
  List<RatingDTO> getTutorRatings(Long personId, Long subjectId);

  @Query(
    "MATCH (n:Person)-[:GAVE]->(lesson:Lesson {canceled: false}) WHERE ID(n) = $tutorId " +
    "AND lesson.date > datetime({timezone: 'Europe/Warsaw', epochSeconds: $dateInMillis/1000}) " +
    "RETURN lesson.date AS dates"
  )
  List<ZonedDateTime> getBusyTimeslots(Long tutorId, Long dateInMillis);

  @Query(
    "MATCH (n:Person)-[:GAVE]->(lesson:Lesson {canceled: false}) WHERE ID(n) = $tutorId " +
    "AND lesson.date > datetime() " +
    "RETURN lesson.date AS dates"
  )
  List<ZonedDateTime> getBusyTimeslots(Long tutorId);

  @Query(
    "MATCH (n:Person) WHERE ID(n) = $personId WITH n, n.photoPath as old SET n.photoPath = $photoPath RETURN old"
  )
  String setPhotoPath(Long personId, String photoPath);
}