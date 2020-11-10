package pl.edu.agh.pawicao.studying_squirrels_api.model.api;

import lombok.Data;

@Data
public class HomeworkEditRequest {
  Long homeworkId;
  Long deadline;
  String textContent;
}
