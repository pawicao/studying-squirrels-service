package pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DetailedPersonAcquaintanceDTO extends DetailedPersonDTO {
  private String email;
  private String phone;
}
