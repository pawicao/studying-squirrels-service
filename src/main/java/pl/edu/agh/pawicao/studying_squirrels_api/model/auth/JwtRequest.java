package pl.edu.agh.pawicao.studying_squirrels_api.model.auth;

import java.io.Serializable;

public class JwtRequest implements Serializable {
  private static final long serialVersionUID = 7839905187546231472L;

  private String email;
  private String password;

  public JwtRequest() {}

  public JwtRequest(String username, String password) {
    this.setEmail(username);
    this.setPassword(password);
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String username) {
    this.email = username;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}