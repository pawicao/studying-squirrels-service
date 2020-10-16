package pl.edu.agh.pawicao.studying_squirrels_api.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.PersonCredentialsProjection;

import java.util.List;

public interface PersonRepository extends Neo4jRepository<Person, Long> {

  boolean existsByEmail(String email);

  PersonCredentialsProjection findPersonByEmail(String email);

  List<Person> findAllByTutorIsTrue();

  // TODO: Rating altering as transactions
  // TODO: Sorting in front-end
  // TODO: Uniqueness! Later on

  // TUTORS

  @Query(
    "MATCH (sub:Subject)<-[offer:OFFERS]-(tutor:Person)-[tutorPlace:LIVES_IN]->(tutorCity:City), (student:Person) " +
      "WHERE tutor.tutor = true AND ID(student) = $id AND ID(tutor) <> $id " +
      "AND ($cityName is null OR tutorCity.name = $cityName) " +
      "AND ($rating is null OR tutor.tutorRating >= $rating) " +
      "AND ($subjects is null OR ANY(subject in $subjects WHERE sub.name = subject)) " +
      "AND ($maxPrice is null OR offer.price <= $maxPrice) " +
      "RETURN tutor as person, tutorPlace as placeOfResidence, tutorCity as city, offer as offeredSubject, sub as subject"
  )
  List<Person> findTutors(Long id, String cityName, Double rating, List<String> subjects, Double maxPrice);

  @Query(
    "MATCH (tutor:Person)-[tutorPlace:LIVES_IN]->(city:City)<-[studentPlace:LIVES_IN]-(student:Person) " +
      "WHERE tutor.tutor = true AND ID(student) = $id AND ID(tutor) <> $id " +
      "AND tutorPlace.postalCode = studentPlace.postalCode " +
      "AND ($rating is null OR tutor.tutorRating >= $rating) " +
      "AND ($subjects is null OR ANY(subject in $subjects WHERE sub.name = subject)) " +
      "AND ($maxPrice is null OR offer.price <= $maxPrice) " +
      "RETURN tutor as person, tutorPlace as placeOfResidence, city"
  )
  List<Person> findNearTutors(Long id, Double rating, List<String> subjects, Double maxPrice);

  // ACQUAINTANCES

}