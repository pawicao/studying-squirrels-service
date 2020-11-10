package pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.projection.Acquaintance;

import lombok.Data;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.BasicPersonAcquaintanceDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.util.CustomLocalDateConverter;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
public class BasicAcquaintanceDTO {
  Long id;
  private BasicPersonAcquaintanceDTO friendOne;
  private BasicPersonAcquaintanceDTO friendTwo;

  @Property("since")
  private ZonedDateTime friendsSince;
}
