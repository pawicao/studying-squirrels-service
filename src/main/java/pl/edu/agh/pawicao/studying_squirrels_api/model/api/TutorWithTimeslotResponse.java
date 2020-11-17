package pl.edu.agh.pawicao.studying_squirrels_api.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.BasicTutorDTO;

@Data @AllArgsConstructor
public class TutorWithTimeslotResponse {
  private BasicTutorDTO tutor;
  private String timeslot;
  private Double lowestPrice;
}
