package pl.edu.agh.pawicao.studying_squirrels_api.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Offer;

import java.util.List;

public interface OfferRepository extends Neo4jRepository<Offer, Long> {
  @Query("MATCH (n)-[o:OFFERS {active: true}]->(m) where ID(n)=$tutorId return n,o,m")
  List<Offer> findAllByTutorId(Long tutorId);

  @Query("MATCH ()-[o:OFFERS]->() WHERE ID(o) = $offerId SET o.active = false return ID(o)")
  Long deleteOfferById(Long offerId);

  @Query("MATCH (n:Person)-[o:OFFERS]->(s:Subject) WHERE ID(n) = $tutorId AND ID(s) = $subjectId RETURN n,o,s")
  Offer findBySubjectIdAndTutorId(Long subjectId, Long tutorId);
}
