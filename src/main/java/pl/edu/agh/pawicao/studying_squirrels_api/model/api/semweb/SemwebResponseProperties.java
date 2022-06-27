package pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SemwebResponseProperties {
  Boolean isCacheSeeked;
  Boolean isDbpediaSeeked;
  Double confidenceRate;
  Double relatednessRate;
  private List<String> spotlightEntities = new ArrayList<>();
}
