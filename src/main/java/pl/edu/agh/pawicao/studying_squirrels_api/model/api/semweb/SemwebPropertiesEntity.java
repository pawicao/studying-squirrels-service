package pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb;

import lombok.Data;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.SemwebEntity;

import java.util.ArrayList;
import java.util.List;

@Data
public class SemwebPropertiesEntity { // TODO: Edge case gdy tylko mamy jedno entity ze spotlighta

  private static SemwebPropertiesEntity mapToPropertyEntity(SemwebEntity semwebEntity) {
    SemwebPropertiesEntity semwebPropertiesEntity = new SemwebPropertiesEntity();
    semwebPropertiesEntity.setName(semwebEntity.getName());
    semwebPropertiesEntity.setUri(semwebEntity.getUri());
    semwebPropertiesEntity.setWikipediaUrl(semwebEntity.getWikipediaUrl());
    return semwebPropertiesEntity;
  }

  public static List<SemwebPropertiesEntity> mapToPropertyEntities(
      List<SemwebEntity> semwebEntities) {
    List<SemwebPropertiesEntity> result = new ArrayList<>();
    for (final SemwebEntity semwebEntity : semwebEntities) {
      result.add(mapToPropertyEntity(semwebEntity));
    }
    return result;
  }

  private String uri;
  private String name;
  private String wikipediaUrl;
}
