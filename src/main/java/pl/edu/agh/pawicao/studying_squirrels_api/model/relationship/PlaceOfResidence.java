package pl.edu.agh.pawicao.studying_squirrels_api.model.relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.neo4j.ogm.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.City;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;

@RelationshipEntity(type = "LIVES_IN")
@Data
public class PlaceOfResidence {

  @Id
  @GeneratedValue
  private Long id;

  @JsonIgnore
  @StartNode
  private Person person;

  @JsonIgnoreProperties({"citizens", "lessons"})
  @EndNode
  private City city;

  private String street;

  private String postalCode;

}
