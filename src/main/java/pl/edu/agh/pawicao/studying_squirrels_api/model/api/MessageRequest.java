package pl.edu.agh.pawicao.studying_squirrels_api.model.api;

import lombok.Data;

@Data
public class MessageRequest {
  private Long senderId;
  private Long receiverId;
  private String text;
  private Long dateInMillis;
}
