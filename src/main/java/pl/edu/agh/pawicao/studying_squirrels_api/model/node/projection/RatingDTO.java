package pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection;

import lombok.Data;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.ZonedDateTime;

@QueryResult
@Data
public class RatingDTO {
  Double rating;
  String ratingDescription;
  ZonedDateTime date;
  String subject;
}
