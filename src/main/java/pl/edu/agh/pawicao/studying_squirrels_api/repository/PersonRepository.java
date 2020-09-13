package pl.edu.agh.pawicao.studying_squirrels_api.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.PersonCredentialsProjection;

public interface PersonRepository extends Neo4jRepository<Person, Long> {
  // TODO: Sprawdzenie czy ktos istnieje z mailem, przeniesienie interfejsu na trzy projectionsy i repozytoria (ale w sumie po co)
  PersonCredentialsProjection findPersonByEmail(String email);

  boolean existsByEmail(String email);

}