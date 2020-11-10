package pl.edu.agh.pawicao.studying_squirrels_api.model.api;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class OfferEditRequest {
  private Long offerId;
  private Map<String, List<String>> slots;
  private Double price;
}
