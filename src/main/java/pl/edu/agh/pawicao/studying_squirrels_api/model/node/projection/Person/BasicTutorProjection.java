package pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person;

import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Offer;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.PlaceOfResidence;

import java.util.List;

public interface BasicTutorProjection extends BasicPersonProjection {

  List<Offer> getOfferedSubjects();
  PlaceOfResidence getPlaceOfResidence();

}
