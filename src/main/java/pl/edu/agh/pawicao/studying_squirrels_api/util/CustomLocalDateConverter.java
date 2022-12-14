package pl.edu.agh.pawicao.studying_squirrels_api.util;

import org.neo4j.ogm.typeconversion.AttributeConverter;

import java.time.LocalDate;

public class CustomLocalDateConverter implements AttributeConverter<LocalDate, LocalDate> {

  @Override
  public LocalDate toGraphProperty(LocalDate value) {
    return value;
  }

  @Override
  public LocalDate toEntityAttribute(LocalDate value) {
    return value;
  }
}