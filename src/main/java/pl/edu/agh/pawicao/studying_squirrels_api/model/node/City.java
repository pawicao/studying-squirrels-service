package pl.edu.agh.pawicao.studying_squirrels_api.model.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.PlaceOfLesson;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.PlaceOfResidence;

import java.util.ArrayList;
import java.util.List;

@NodeEntity
@Getter @Setter
public class City {

  @Id
  @GeneratedValue
  private Long id;

  private String name;

  @Relationship(type = "LIVES_IN", direction = "INCOMING")
  private List<PlaceOfResidence> citizens = new ArrayList<>();

  @Relationship(type = "WAS_IN", direction = "INCOMING")
  private List<PlaceOfLesson> lessons = new ArrayList<>();

}
