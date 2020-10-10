package pl.edu.agh.pawicao.studying_squirrels_api.model.relationship;

import lombok.Data;
import org.neo4j.ogm.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Lesson;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;

@RelationshipEntity(type = "TOOK")
@Data
public class TakenLesson {

  @Id
  @GeneratedValue
  private Long id;

  @StartNode
  private Person student;

  @EndNode
  private Lesson lesson;

  private Double tutorRating;

  private String tutorRatingDescription;

}
