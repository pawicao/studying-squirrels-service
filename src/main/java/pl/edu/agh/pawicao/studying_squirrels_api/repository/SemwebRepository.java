package pl.edu.agh.pawicao.studying_squirrels_api.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.SemwebEntity;

import java.util.List;

public interface SemwebRepository extends Neo4jRepository<SemwebEntity, Long> {

  @Query(
      "MATCH (firstEntity:SemwebEntity {uri: $firstUri})-[isRelated:IS_RELATED]-(secondEntity:SemwebEntity {uri: $secondUri}) "
          + "WHERE isRelated.numberOfConnections/$rateDivider/isRelated.shortestDistance > $relatednessRate "
          + "RETURN firstEntity, isRelated, secondEntity")
  List<SemwebEntity> findPairsByRelatedness(
      String firstUri, String secondUri, double relatednessRate, int rateDivider);

  @Query(
      "MATCH (firstEntity:SemwebEntity {uri: $firstUri})-[isRelated:IS_RELATED]-(secondEntity:SemwebEntity {uri: $secondUri}) "
          + "RETURN firstEntity, isRelated, secondEntity")
  List<SemwebEntity> findPairByUris(String firstUri, String secondUri);

  @Query(
      "MATCH (:SemwebEntity {uri: $firstUri})-[isRelated:IS_RELATED]-(:SemwebEntity {uri: $secondUri}) "
          + "SET isRelated = {numberOfConnections: $numberOfConnections, shortestDistance: $shortestDistance} "
          + "RETURN true")
  boolean updatePairConnection(
      String firstUri, String secondUri, int numberOfConnections, int shortestDistance);

  @Query(
      "MERGE (firstEntity:SemwebEntity {uri: $firstUri, name: $firstName, wikipediaUrl: $firstWikipediaUrl}) "
          + "MERGE (secondEntity:SemwebEntity {uri: $secondUri, name: $secondName, wikipediaUrl: $secondWikipediaUrl}) "
          + "MERGE (firstEntity)-[isEntity:IS_RELATED {numberOfConnections: $numberOfConnections, shortestDistance: $shortestDistance}]-(secondEntity) "
          + "RETURN true")
  boolean createPairConnection(
      String firstUri,
      String firstName,
      String firstWikipediaUrl,
      String secondUri,
      String secondName,
      String secondWikipediaUrl,
      int numberOfConnections,
      int shortestDistance);
}
