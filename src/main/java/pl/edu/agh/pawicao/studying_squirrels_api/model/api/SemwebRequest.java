package pl.edu.agh.pawicao.studying_squirrels_api.model.api;

import lombok.Data;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb.SemwebRequestProperties;

@Data
public class SemwebRequest {
    String text;
    SemwebRequestProperties properties;
}
