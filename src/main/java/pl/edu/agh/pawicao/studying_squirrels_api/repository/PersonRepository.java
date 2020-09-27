package pl.edu.agh.pawicao.studying_squirrels_api.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.BasicTutorProjection;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.PersonCredentialsProjection;

import java.util.List;

public interface PersonRepository extends Neo4jRepository<Person, Long> {

  boolean existsByEmail(String email);
  PersonCredentialsProjection findPersonByEmail(String email);
  
  @Query(value = "MATCH (n:`Person`) WHERE n.`tutor` = true WITH n RETURN n")
  List<Person> findAllTutors();

  List<Person> findAllByTutorIsTrue();

  // TODO: Ogarnac czemu tu projection nie dzialaaa
}