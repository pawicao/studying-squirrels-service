package pl.edu.agh.pawicao.studying_squirrels_api.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Acquaintance;

import java.util.List;

public interface AcquaintanceRepository extends Neo4jRepository<Acquaintance, Long> {

  @Query(
    "MATCH (me:Person), (someone:Person) " +
    "WHERE ID(me) = $idOne AND ID(someone) = $idTwo " +
    "CREATE (me)-[a:IS_FRIEND {accepted: false, friendsSince: datetime({timezone: 'Europe/Warsaw'})}]->(someone) " +
    "RETURN a, me, someone"
  )
  Acquaintance createContactRequest(Long idOne, Long idTwo);

  @Query(
    "MATCH (someone:Person)-[a:IS_FRIEND]->(me:Person) " +
    "WHERE ID(someone) = $idTwo AND ID(me) = $idOne " +
    "OPTIONAL MATCH (s:Subject)<-[o:OFFERS]-(someone) " +
    "SET a.accepted = true " +
    "RETURN a, someone, me, o, s"
  )
  Acquaintance acceptContactRequest(Long idOne, Long idTwo);

  @Query(
    "MATCH (one:Person)-[a:IS_FRIEND]-(two:Person) " +
    "WHERE ID(one) = $idOne AND ID(two) = $idTwo " +
    "DELETE a " +
    "RETURN ID(a)"
  )
  Long deleteContact(Long idOne, Long idTwo);

  @Query(
    "MATCH (p1:Person)-[i:IS_FRIEND]-(p2:Person) WHERE ID(p1) = $idOne AND ID(p2) = $idTwo RETURN p1, i, p2"
  )
  Acquaintance getContactStatus(Long idOne, Long idTwo);

  @Query(
    "MATCH (n:Person)-[:IS_FRIEND {accepted: true}]-(m:Person) " +
    "WHERE ID(n) = $id " +
    "RETURN m ORDER BY m.lastName, n.firstName"
  )
  List<Person> findAllAcquaintances(Long id);

  @Query(
    "MATCH (n:Person)-[a:IS_FRIEND {accepted: false}]->(m:Person) " +
    "WHERE ID(n) = $id " +
    "RETURN m ORDER BY a.since"
  )
  List<Person> findSentAwaitingAcquaintances(Long id);

  @Query(
    "MATCH (n:Person)<-[a:IS_FRIEND {accepted: false}]-(m:Person) " +
    "WHERE ID(n) = $id " +
    "RETURN m ORDER BY a.since"
  )
  List<Person> findReceivedAwaitingAcquaintances(Long id);
}
