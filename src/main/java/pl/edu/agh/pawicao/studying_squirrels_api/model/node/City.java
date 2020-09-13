package pl.edu.agh.pawicao.studying_squirrels_api.model.node;

import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.PlaceOfLesson;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.PlaceOfResidence;

import java.util.ArrayList;
import java.util.List;

@NodeEntity
@Data
public class City {

  private String name;

  @Relationship(type = "LIVES_IN", direction = "INCOMING")
  private List<PlaceOfResidence> citizens = new ArrayList<>();

  @Relationship(type = "WAS_IN", direction = "INCOMING")
  private List<PlaceOfLesson> lessons = new ArrayList<>();

}
