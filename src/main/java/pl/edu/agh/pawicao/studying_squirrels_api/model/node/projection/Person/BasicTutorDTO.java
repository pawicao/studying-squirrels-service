package pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person;

import lombok.Data;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Offer;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.PlaceOfResidence;

import java.util.List;

@Data
public class BasicTutorDTO extends BasicPersonDTO {
  private List<Offer> offeredSubjects;
  private PlaceOfResidence placeOfResidence;
  private Double tutorRating;
  private Integer tutorRatingsGiven;
}
