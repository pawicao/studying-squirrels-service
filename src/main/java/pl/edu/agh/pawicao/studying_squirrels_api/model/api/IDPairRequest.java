package pl.edu.agh.pawicao.studying_squirrels_api.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class IDPairRequest {
  private Long idOne;
  private Long idTwo;
}
