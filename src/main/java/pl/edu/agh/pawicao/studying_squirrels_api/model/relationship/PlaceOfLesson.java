package pl.edu.agh.pawicao.studying_squirrels_api.model.relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.neo4j.ogm.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.City;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Lesson;

@RelationshipEntity(type = "WAS_IN")
@Data
public class PlaceOfLesson {

  @GeneratedValue
  @Id
  private Long id;

  @StartNode
  private Lesson lesson;

  @JsonIgnoreProperties({"citizens", "lessons"})
  @EndNode
  private City city;

  private String street;

  private String postalCode;
}
