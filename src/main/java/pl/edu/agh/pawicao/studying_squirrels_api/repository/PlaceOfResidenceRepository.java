package pl.edu.agh.pawicao.studying_squirrels_api.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.PlaceOfResidence;

public interface PlaceOfResidenceRepository extends Neo4jRepository<PlaceOfResidence, Long> {
}
