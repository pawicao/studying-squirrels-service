package pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person;

import lombok.Data;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.ContactInfo;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Offer;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.PlaceOfResidence;

import java.util.List;

@Data
public class DetailedPersonDTO extends BasicPersonDTO {
  private String lastName;
  private Double tutorRating;
  private Integer tutorRatingsGiven;
  private Double studentRating;
  private Integer studentRatingsGiven;
  private List<Offer> offeredSubjects;
  private PlaceOfResidence placeOfResidence;
}
