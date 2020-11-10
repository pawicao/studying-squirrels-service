package pl.edu.agh.pawicao.studying_squirrels_api.model.api;

import lombok.Data;

@Data
public class LessonRequest {
  private Long studentId;
  private Long offerId;
  private Long dateInMillis;
  private String studentDescription;
}
