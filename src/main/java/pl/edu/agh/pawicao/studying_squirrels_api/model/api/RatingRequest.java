package pl.edu.agh.pawicao.studying_squirrels_api.model.api;

import lombok.Data;

@Data
public class RatingRequest {
  Long lessonId;
  boolean student;
  boolean altering;
  Double rating;
  String ratingDescription;
}
