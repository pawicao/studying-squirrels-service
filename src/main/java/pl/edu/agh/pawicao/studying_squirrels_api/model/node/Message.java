package pl.edu.agh.pawicao.studying_squirrels_api.model.node;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.annotation.typeconversion.DateLong;
import pl.edu.agh.pawicao.studying_squirrels_api.util.CustomLocalDateTimeConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@NodeEntity
@Data
public class Message {

  @Id
  @GeneratedValue
  private Long id;

  private String text;

  private ZonedDateTime date;

  @Relationship(type = "SENT", direction = "INCOMING")
  private Person sender;

  @Relationship(type = "RECEIVED")
  private Person receiver;

}
