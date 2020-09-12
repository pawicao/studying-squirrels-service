package pl.edu.agh.pawicao.studying_squirrels_api.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;

import java.util.List;

public interface PersonRepository extends Neo4jRepository<Person, Long> {
  Person findPersonByEmail(String email);
}