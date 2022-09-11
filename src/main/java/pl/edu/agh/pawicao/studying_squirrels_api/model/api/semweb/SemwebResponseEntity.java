package pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb;

import lombok.Data;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.SemwebEntity;

import java.util.Objects;

@Data
public class SemwebResponseEntity {
  private String uri;
  private String name;
  private String wikipediaUrl;
  private int occurrences;

  public SemwebResponseEntity(SemwebEntity semwebEntity) {
    this.uri = semwebEntity.getUri();
    this.name = semwebEntity.getName();
    this.wikipediaUrl = semwebEntity.getWikipediaUrl();
    this.occurrences = 0;
  }

  public SemwebResponseEntity(SemwebEntity semwebEntity, int occurrences) {
    this.uri = semwebEntity.getUri();
    this.name = semwebEntity.getName();
    this.wikipediaUrl = semwebEntity.getWikipediaUrl();
    this.occurrences = occurrences;
  }

  public SemwebResponseEntity(String uri, String name, String wikipediaUrl, int occurrences) {
    this.uri = uri;
    this.name = name;
    this.wikipediaUrl = wikipediaUrl;
    this.occurrences = occurrences;
  }

  public SemwebResponseEntity(String uri, String name, String wikipediaUrl) {
    this.uri = uri;
    this.name = name;
    this.wikipediaUrl = wikipediaUrl;
    this.occurrences = 0;
  }

  public boolean equals(Object obj) {
    if (obj instanceof SemwebResponseEntity) {
      SemwebResponseEntity se = (SemwebResponseEntity) obj;
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
