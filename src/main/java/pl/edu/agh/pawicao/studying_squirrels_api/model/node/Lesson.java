package pl.edu.agh.pawicao.studying_squirrels_api.model.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.GivenLesson;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.PlaceOfLesson;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.TakenLesson;
import pl.edu.agh.pawicao.studying_squirrels_api.util.CustomLocalDateConverter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@NodeEntity
@Getter
@Setter
public class Lesson {

  @Id
  @GeneratedValue
  private Long id;

  private boolean confirmed = false;

  private boolean canceled = false;

  private ZonedDateTime date;

  private String studentDescription;

  private String tutorDescription;

  @Relationship(type = "HAS")
  private List<Homework> homeworks = new ArrayList<>();

  @JsonIgnoreProperties({"lessons", "offeringTutors"})
  @Relationship(type = "IS_OF")
  private Subject subject;

  @JsonIgnoreProperties("lesson")
  @Relationship(type = "TOOK", direction = "INCOMING")
  private TakenLesson takenLesson;

  @JsonIgnoreProperties("lesson")
  @Relationship(type = "GAVE", direction = "INCOMING")
  private GivenLesson givenLesson;

  @JsonIgnoreProperties("lesson")
  @Relationship(type = "WAS_IN")
  private PlaceOfLesson place;

}
