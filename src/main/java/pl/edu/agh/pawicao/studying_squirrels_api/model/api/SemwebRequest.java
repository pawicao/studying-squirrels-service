package pl.edu.agh.pawicao.studying_squirrels_api.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb.SemwebRequestProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SemwebRequest {
  String text;
  SemwebRequestProperties properties;
}
