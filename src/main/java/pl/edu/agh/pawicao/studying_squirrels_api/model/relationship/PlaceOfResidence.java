package pl.edu.agh.pawicao.studying_squirrels_api.model.relationship;

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

  @StartNode
  private Person person;

  @EndNode
  private City city;

  private String street;

  @Property("postal_code")
  private String postalCode;

}
