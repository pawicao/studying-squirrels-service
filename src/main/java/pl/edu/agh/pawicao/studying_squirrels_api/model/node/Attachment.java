package pl.edu.agh.pawicao.studying_squirrels_api.model.node;

import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
@Data
public class Attachment {

  private String filePath;

  @Relationship(type = "CONTAINS", direction = "INCOMING")
  private Homework homework;

}
