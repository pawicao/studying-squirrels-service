package pl.edu.agh.pawicao.studying_squirrels_api.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;

public interface PersonRepository extends Neo4jRepository<Person, Long> {
  // TODO: Sprawdzenie czy ktos istnieje z mailem
  Person findPersonByEmail(String email);
}