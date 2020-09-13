package pl.edu.agh.pawicao.studying_squirrels_api.model.node;

import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NodeEntity
@Data
public class Homework {

  private LocalDate deadline;

  private boolean done;

  @Property("text_content")
  private String textContent;

  @Relationship(type = "CONTAINS")
  private List<Attachment> attachments = new ArrayList<>();

  @Relationship(type = "HAS", direction = "INCOMING")
  private Lesson lesson;

}
