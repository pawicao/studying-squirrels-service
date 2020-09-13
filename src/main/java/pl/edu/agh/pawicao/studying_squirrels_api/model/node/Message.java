package pl.edu.agh.pawicao.studying_squirrels_api.model.node;

import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.LocalDate;

@NodeEntity
@Data
public class Message {

  private String text;

  private LocalDate date;

  @Relationship(type = "SENT", direction = "INCOMING")
  private Person sender;

  @Relationship(type = "RECEIVED")
  private Person receiver;

}
