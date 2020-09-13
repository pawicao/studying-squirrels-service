package pl.edu.agh.pawicao.studying_squirrels_api.model.node;

import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.GivenLesson;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.PlaceOfLesson;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.TakenLesson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NodeEntity
@Data
public class Lesson {

  private boolean confirmed;

  private LocalDate date;

  private String description;

  @Relationship(type = "HAS")
  private List<Homework> homeworks = new ArrayList<>();

  @Relationship(type = "IS_OF")
  private Subject subject;

  @Relationship(type = "TOOK", direction = "INCOMING")
  private TakenLesson takenLesson;

  @Relationship(type = "GAVE", direction = "INCOMING")
  private GivenLesson givenLesson;

  @Relationship(type = "WAS_IN")
  private PlaceOfLesson place;

}
