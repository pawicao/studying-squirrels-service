package pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person;

import lombok.Data;

@Data
public class DetailedPersonAcceptDTO extends DetailedPersonDTO {
  private String email;
  private String phone;
}
