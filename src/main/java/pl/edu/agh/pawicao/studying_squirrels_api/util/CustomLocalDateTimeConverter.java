package pl.edu.agh.pawicao.studying_squirrels_api.util;

import org.neo4j.ogm.typeconversion.AttributeConverter;

import java.time.LocalDateTime;

public class CustomLocalDateTimeConverter implements AttributeConverter<LocalDateTime, LocalDateTime> {

  @Override public LocalDateTime toGraphProperty(LocalDateTime value) {
    return value;
  }

  @Override public LocalDateTime toEntityAttribute(LocalDateTime value) {
    return value;
  }
}