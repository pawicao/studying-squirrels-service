package pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Offer;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.PlaceOfResidence;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class BasicTutorDTO extends BasicPersonDTO {
  @JsonIgnoreProperties("timeslots")
  private List<Offer> offeredSubjects;
  private PlaceOfResidence placeOfResidence;
  private Double tutorRating;
  private Integer tutorRatingsGiven;
}
