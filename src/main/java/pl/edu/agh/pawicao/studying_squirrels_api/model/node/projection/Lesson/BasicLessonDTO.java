package pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Lesson;

import lombok.Data;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Homework;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Subject;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class BasicLessonDTO {
  Long id;
  private boolean confirmed;
  private boolean canceled;
  private ZonedDateTime date;
  private String studentDescription;
  private String tutorDescription;
  private List<Homework> homeworks = new ArrayList<>();
  private Subject subject;

}
