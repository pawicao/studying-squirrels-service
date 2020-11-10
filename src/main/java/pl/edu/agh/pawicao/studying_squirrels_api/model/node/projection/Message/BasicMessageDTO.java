package pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Message;


import lombok.Data;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.BasicPersonAcquaintanceDTO;

import java.time.ZonedDateTime;

@Data
public class BasicMessageDTO {

  Long id;
  private String text;
  private ZonedDateTime date;
  private BasicPersonAcquaintanceDTO sender;
  private BasicPersonAcquaintanceDTO receiver;

}
