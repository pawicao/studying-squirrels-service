package pl.edu.agh.pawicao.studying_squirrels_api.model.api;

import lombok.Data;

@Data
public class HomeworkSolutionRequest {
  private Long id;
  private Long date;
  private String solution;
}
