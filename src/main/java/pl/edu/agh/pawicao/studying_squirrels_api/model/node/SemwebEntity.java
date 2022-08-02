package pl.edu.agh.pawicao.studying_squirrels_api.model.node;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.SemwebEntityConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NodeEntity
@Data
public class SemwebEntity {

  public SemwebEntity(String uri, String name, String link) {
    this.uri = uri;
    this.name = name;
    this.wikipediaUrl = link;
  }

  @Id @GeneratedValue private Long id;

  private String uri;
  private String name;
  private String wikipediaUrl;

  @Relationship(type = "IS_RELATED")
  private List<SemwebEntityConnection> relatedEntities = new ArrayList<>();

  @Relationship(type = "IS_RELATED", direction = "INCOMING")
  private List<SemwebEntityConnection> relatedEntitiesIncoming = new ArrayList<>();

  public boolean equals(Object obj) {
    if (obj instanceof SemwebEntity) {
      SemwebEntity se = (SemwebEntity) obj;
      return (Objects.equals(this.uri, se.uri));
    }
    return false;
  }

  public int hashCode() {
    char[] chars = new char[this.uri.length()];
    this.uri.getChars(0, this.uri.length(), chars, 0);

    int returnValueInt = 0;
    for (char aChar : chars) {
      returnValueInt = returnValueInt + aChar;
    }
    return (300 * returnValueInt);
  }
}
