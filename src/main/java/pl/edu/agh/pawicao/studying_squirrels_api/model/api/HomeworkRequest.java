package pl.edu.agh.pawicao.studying_squirrels_api.model.api;

import lombok.Data;

@Data
public class HomeworkRequest {
  Long lessonId;
  Long deadline;
  String textContent;
}
