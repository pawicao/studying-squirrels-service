package pl.edu.agh.pawicao.studying_squirrels_api.model.node;

import lombok.Data;
import org.neo4j.ogm.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NodeEntity
@Data
public class Homework {

  @Id
  @GeneratedValue
  private Long id;

  private LocalDate deadline;

  private boolean done;

  private String textContent;

  @Relationship(type = "CONTAINS")
  private List<Attachment> attachments = new ArrayList<>();

  @Relationship(type = "HAS", direction = "INCOMING")
  private Lesson lesson;

}
