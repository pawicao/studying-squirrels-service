package pl.edu.agh.pawicao.studying_squirrels_api.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.SemwebEntity;

import java.util.List;

public interface SemwebRepository extends Neo4jRepository<SemwebEntity, String> {

  @Query(
      "MATCH (firstEntity:SemwebEntity {uri: $firstUri})-[isEntity:IS_ENTITY]-(secondEntity:SemwebEntity {uri: $secondUri}) "
          + "RETURN firstEntity, isEntity, secondEntity")
  List<SemwebEntity> findPairByUris(String firstUri, String secondUri);

  @Query(
      "MERGE (firstEntity:SemwebEntity {uri: $firstUri})-"
          + "[isEntity:IS_ENTITY {numberOfConnections: $numberOfConnections, shortestDistance: $shortestDistance}]"
          + "-(secondEntity:SemwebEntity {uri: $secondUri}) "
          + "RETURN true") // TODO: To tak nie zadziala, trzeba update
  boolean updatePairConnection(
      String firstUri, String secondUri, int numberOfConnections, int shortestDistance);
}
