package pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SemwebRequestProperties {
  Boolean isCacheSeeked;
  Double confidenceRate;
  Double relatednessRate;
  private List<SemwebPropertiesEntity> extractedEntities = new ArrayList<>();
  private List<String> spotlightEntities = new ArrayList<>();
}
