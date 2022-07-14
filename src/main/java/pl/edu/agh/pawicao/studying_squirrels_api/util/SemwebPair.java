package pl.edu.agh.pawicao.studying_squirrels_api.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.SemwebEntity;

@Data
@AllArgsConstructor
public class SemwebPair {
  private SemwebEntity first;
  private SemwebEntity second;
  private int shortestDistance;
  private int numberOfConnections;
}
