package pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person;

import lombok.Data;
import org.neo4j.ogm.annotation.Property;

import java.time.LocalDate;

@Data
public class BasicAcquaintanceDTO {
  Long id;
  private BasicPersonAcquaintanceDTO friendOne;
  private BasicPersonAcquaintanceDTO friendTwo;
  @Property("since")
  private LocalDate friendsSince;
}
