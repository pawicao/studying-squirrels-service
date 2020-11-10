package pl.edu.agh.pawicao.studying_squirrels_api.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Message;

import java.util.List;

public interface MessageRepository extends Neo4jRepository<Message, Long> {
  @Query(
    "MATCH (sender:Person), (receiver:Person) " +
    "WHERE ID(sender) = $senderId AND ID(receiver) = $receiverId " +
    "CREATE (sender)-[x:SENT]->(m:Message {text: $text, date: datetime({epochSeconds: $dateInMillis/1000})})" +
    "-[y:RECEIVED]->(receiver) " +
    "RETURN m, sender, receiver, x, y"
  )
  Message addMessage(Long senderId, Long receiverId, String text, Long dateInMillis);

  @Query(
    "MATCH (me:Person)-[s]-(m:Message)-[r]-(someone:Person) " +
    "WHERE ID(me) = $myId AND ID(someone) = $someoneId " +
    "RETURN me, s, m, r, someone " +
    "ORDER BY m.date"
  )
  List<Message> getMessages(Long myId, Long someoneId);

  @Query(
    "MATCH (me:Person)-[s]-(:Message)-[r]-(someone:Person) WHERE ID(me) = $id " +
    "CALL { WITH someone MATCH (me:Person)-[]-(mes:Message)-[]-(someone:Person) " +
    "RETURN mes ORDER BY mes.date DESC LIMIT 1 } " +
    "RETURN me, mes, someone, s, r"
  )
  List<Message> getAllMessages(Long id);
}
