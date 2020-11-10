package pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.projection.Offer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Subject;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Offer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class OfferDTO {
  Long id;
  @JsonIgnoreProperties("offeringTutors")
  private Subject subject;
  private Double price;
  private Map<String, List<String>> timeslots = new HashMap<>();

  public static OfferDTO convertToDTO(Offer offer) {
    OfferDTO dto = new OfferDTO();
    dto.setSubject(offer.getSubject());
    dto.setId(offer.getId());
    dto.setPrice(offer.getPrice());
    Map<String, List<String>> slots = slotsAsList(offer.getTimeslots());
    dto.setTimeslots(slots);
    return dto;
  }

  public static Map<String, List<String>> slotsAsList (Map <String, String> timeslots) {
    return timeslots.entrySet().stream().collect(Collectors.toMap(
      e -> e.getKey(),
      e -> Stream.of(e.getValue().split(","))
        .map(String::trim)
        .collect(Collectors.toList())
    ));
  }
}
