package pl.edu.agh.pawicao.studying_squirrels_api.model.relationship;

import lombok.Data;
import org.neo4j.ogm.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Subject;

@RelationshipEntity(type = "IS_OF")
@Data
public class Offer {

  @GeneratedValue
  @Id
  private Long id;

  @StartNode
  private Person tutor;

  @EndNode
  private Subject subject;

  private Double price;

}
