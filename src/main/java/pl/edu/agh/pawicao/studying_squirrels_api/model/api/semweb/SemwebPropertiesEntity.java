package pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SemwebPropertiesEntity {

  private static SemwebPropertiesEntity mapToPropertyEntity(SemwebResponseEntity semwebEntity) {
    SemwebPropertiesEntity semwebPropertiesEntity = new SemwebPropertiesEntity();
    semwebPropertiesEntity.setName(semwebEntity.getName());
    semwebPropertiesEntity.setUri(semwebEntity.getUri());
    semwebPropertiesEntity.setWikipediaUrl(semwebEntity.getWikipediaUrl());
    return semwebPropertiesEntity;
  }

  public static List<SemwebPropertiesEntity> mapToPropertyEntities(
      List<SemwebResponseEntity> semwebEntities) {
    List<SemwebPropertiesEntity> result = new ArrayList<>();
    for (final SemwebResponseEntity semwebEntity : semwebEntities) {
      result.add(mapToPropertyEntity(semwebEntity));
    }
    return result;
  }

  private String uri;
  private String name;
  private String wikipediaUrl;
}
