package pl.edu.agh.pawicao.studying_squirrels_api.model.api;

import lombok.Data;

@Data
public class LessonTutorDescriptionRequest {
  Long lessonId;
  String tutorDescription;
}
