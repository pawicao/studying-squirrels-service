package pl.edu.agh.pawicao.studying_squirrels_api.model.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
@Data
public class Attachment {

  @Id
  @GeneratedValue
  private Long id;

  private String filePath;

  @JsonIgnore
  @Relationship(type = "CONTAINS", direction = "INCOMING")
  private Homework homework;

}
