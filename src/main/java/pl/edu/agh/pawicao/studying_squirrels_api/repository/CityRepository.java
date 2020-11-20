package pl.edu.agh.pawicao.studying_squirrels_api.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.City;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends Neo4jRepository<City, Long> {
  Optional<City> findByName(String name);
  List<City> findAll();
}
