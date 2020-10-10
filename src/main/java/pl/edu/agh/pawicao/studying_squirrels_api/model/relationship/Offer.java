package pl.edu.agh.pawicao.studying_squirrels_api.model.relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.neo4j.ogm.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Subject;

@RelationshipEntity(type = "OFFERS")
@Data
public class Offer {

  @GeneratedValue
  @Id
  private Long id;

  @JsonIgnore
  @StartNode
  private Person tutor;

  @JsonIgnoreProperties("offeringTutors")
  @EndNode
  private Subject subject;

  private Double price;

}
