package pl.edu.agh.pawicao.studying_squirrels_api.model.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.ZonedDateTime;

@NodeEntity
@Data
public class Message {

  @Id
  @GeneratedValue
  private Long id;

  private String text;

  private ZonedDateTime date;

  @JsonIgnoreProperties("sentMessages")
  @Relationship(type = "SENT", direction = "INCOMING")
  private Person sender;

  @JsonIgnoreProperties("receivedMessages")
  @Relationship(type = "RECEIVED")
  private Person receiver;

}
