package pl.edu.agh.pawicao.studying_squirrels_api.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class JwtResponse implements Serializable {

  private static final long serialVersionUID = 2614602602299072305L;
  private final String jwtToken;
  private final Long userId;

}