package pl.edu.agh.pawicao.studying_squirrels_api.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb.SemwebResponseProperties;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.SemwebEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class SemwebResponse {
  private List<SemwebEntity> extractedEntities = new ArrayList<>();
  private SemwebResponseProperties properties;
}
