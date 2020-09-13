package pl.edu.agh.pawicao.studying_squirrels_api.model.relationship;

import lombok.Data;
import org.neo4j.ogm.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;

import java.time.LocalDate;

@RelationshipEntity(type = "IS_FRIEND")
@Data
public class Acquaintance {

  @GeneratedValue
  @Id
  private Long id;

  @StartNode
  private Person friendOne;

  @EndNode
  private Person friendTwo;

  @Property("since")
  private LocalDate friendsSince;

  private boolean accepted;
}
