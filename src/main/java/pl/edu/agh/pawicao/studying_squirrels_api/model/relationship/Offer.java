package pl.edu.agh.pawicao.studying_squirrels_api.model.relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.neo4j.ogm.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Subject;

import java.util.HashMap;
import java.util.Map;

@RelationshipEntity(type = "OFFERS")
@Getter @Setter
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

  private boolean active = true;

  @Properties(allowCast = true)
  private Map<String, String> timeslots = new HashMap<>();
}
