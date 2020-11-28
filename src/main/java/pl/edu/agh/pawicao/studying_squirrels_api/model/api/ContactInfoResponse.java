package pl.edu.agh.pawicao.studying_squirrels_api.model.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.DetailedPersonDTO;

@Getter
@Setter
@AllArgsConstructor
public class ContactInfoResponse {
  private DetailedPersonDTO person;
  private ContactInfo contactInfo;
}
