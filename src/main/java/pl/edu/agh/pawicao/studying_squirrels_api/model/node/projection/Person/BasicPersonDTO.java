package pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person;

import lombok.Data;

@Data
public class BasicPersonDTO {
  private Long id;
  private String firstName;
  private boolean tutor;
  private boolean student;
  private String photoPath;
}
