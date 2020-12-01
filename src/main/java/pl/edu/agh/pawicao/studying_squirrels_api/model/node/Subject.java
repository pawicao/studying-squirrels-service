package pl.edu.agh.pawicao.studying_squirrels_api.model.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Offer;

import java.util.ArrayList;
import java.util.List;

@NodeEntity
@Data
public class Subject {

  @Id
  @GeneratedValue
  private Long id;

  private String name;

  // Icon name from Material Design Community Icons list
  private String icon;

  @Relationship(type = "IS_OF", direction = "INCOMING")
  private List<Lesson> lessons = new ArrayList<>();

  @JsonIgnoreProperties("subject")
  @Relationship(type = "OFFERS", direction = "INCOMING")
  private List<Offer> offeringTutors = new ArrayList<>();

}
