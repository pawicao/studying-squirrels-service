package pl.edu.agh.pawicao.studying_squirrels_api.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb.SemwebResponseEntity;

@Data
@AllArgsConstructor
public class SemwebPair {
  private SemwebResponseEntity first;
  private SemwebResponseEntity second;
  private int shortestDistance;
  private int numberOfConnections;
}
