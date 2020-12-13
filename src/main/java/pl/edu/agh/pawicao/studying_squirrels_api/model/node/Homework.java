package pl.edu.agh.pawicao.studying_squirrels_api.model.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.neo4j.ogm.annotation.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@NodeEntity
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
public class Homework {

  @Id
  @GeneratedValue
  private Long id;

  private ZonedDateTime deadline;

  private ZonedDateTime handedIn;

  private boolean done;

  private String textContent;

  private String solution;

  @Relationship(type = "CONTAINS")
  private List<Attachment> attachments = new ArrayList<>();

  @JsonIgnoreProperties({"homeworks", "place"})
  @Relationship(type = "HAS", direction = "INCOMING")
  private Lesson lesson;

}
