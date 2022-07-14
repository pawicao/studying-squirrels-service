package pl.edu.agh.pawicao.studying_squirrels_api.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb.SemwebResponseEntity;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb.SemwebResponseProperties;

import java.util.List;

@Data
@AllArgsConstructor
public class SemwebResponse {
  private List<SemwebResponseEntity> extractedEntities;
  private SemwebResponseProperties properties;
}
