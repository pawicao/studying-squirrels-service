package pl.edu.agh.pawicao.studying_squirrels_api.model.relationship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.neo4j.ogm.annotation.*;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.annotation.typeconversion.DateLong;
import org.neo4j.ogm.annotation.typeconversion.DateString;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.util.CustomLocalDateConverter;

import java.time.LocalDate;
import java.util.Date;

@RelationshipEntity(type = "IS_FRIEND")
@Data
public class Acquaintance {

  @GeneratedValue
  @Id
  private Long id;

  @JsonIgnoreProperties("friendshipsInitiated")
  @StartNode
  private Person friendOne;

  @JsonIgnoreProperties("friendshipsReceived")
  @EndNode
  private Person friendTwo;

  @Property("since")
  @Convert(CustomLocalDateConverter.class)
  private LocalDate friendsSince;

  private boolean accepted;
}
