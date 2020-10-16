package pl.edu.agh.pawicao.studying_squirrels_api.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Message;

import java.time.LocalDateTime;

public interface MessageRepository extends Neo4jRepository<Message, Long> {
  @Query(
      "MATCH (sender:Person), (receiver:Person) " +
          "WHERE ID(sender) = $senderId AND ID(receiver) = $receiverId " +
          "CREATE (sender)-[:SENT]->(m:Message {text: $text, date: datetime({epochSeconds: $dateInMillis/1000})})" +
          "-[:RECEIVED]->(receiver) " +
          "RETURN m, sender, receiver"
  )
  Message addMessage(Long senderId, Long receiverId, String text, Long dateInMillis);
}
