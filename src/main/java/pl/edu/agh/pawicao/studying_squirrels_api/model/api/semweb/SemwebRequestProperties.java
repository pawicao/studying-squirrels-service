package pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SemwebRequestProperties {
    Boolean isCacheSeeked;
    Boolean isDbpediaSeeked;
    Double confidenceRate;
    Double relatednessRate;
    private List<SemwebPropertiesEntity> extractedEntities = new ArrayList<>();
    private List<SemwebPropertiesEntity> spotlightEntities = new ArrayList<>();
}
