package pl.edu.agh.pawicao.studying_squirrels_api.model.api;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class OfferRequest {
  private Long tutorId;
  private Long subjectId;
  private Map<String, List<String>> slots;
  private Double price;
}
