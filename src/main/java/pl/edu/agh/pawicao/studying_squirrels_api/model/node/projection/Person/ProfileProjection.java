package pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person;

import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Offer;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.PlaceOfResidence;

import java.time.LocalDate;
import java.util.List;

public interface ProfileProjection extends BasicPersonProjection {

  String getLastName();
  LocalDate getDateOfBirth();
  List<Offer> getOfferedSubjects();
  PlaceOfResidence getPlaceOfResidence();

}
