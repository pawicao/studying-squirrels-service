package pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Lesson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Homework;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Subject;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.GivenLesson;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.TakenLesson;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class LessonStudentDTO {
  Long id;
  @JsonIgnoreProperties({"lessons", "offeringTutors"})
  private Subject subject;
  private boolean confirmed;
  private boolean canceled;
  private ZonedDateTime date;
  @JsonIgnoreProperties("lesson")
  private TakenLesson takenLesson;
  @JsonIgnoreProperties("lesson")
  private GivenLesson givenLesson;
  @JsonIgnoreProperties("lesson")
  private List<Homework> homeworks;
}
