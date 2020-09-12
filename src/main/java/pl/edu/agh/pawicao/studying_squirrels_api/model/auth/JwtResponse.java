package pl.edu.agh.pawicao.studying_squirrels_api.model.auth;

import java.io.Serializable;

public class JwtResponse implements Serializable {
  private static final long serialVersionUID = 2614602602299072305L;

  private final String jwtToken;

  public JwtResponse(String jwtToken) {
    this.jwtToken = jwtToken;
  }

  public String getToken() {
    return this.jwtToken;
  }
}