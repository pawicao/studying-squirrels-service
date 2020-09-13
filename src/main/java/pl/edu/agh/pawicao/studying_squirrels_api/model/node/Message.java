package pl.edu.agh.pawicao.studying_squirrels_api.model.node;

import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;

import java.time.LocalDate;

@NodeEntity
@Data
public class Message {

  private String text;

  private LocalDate date;

}
