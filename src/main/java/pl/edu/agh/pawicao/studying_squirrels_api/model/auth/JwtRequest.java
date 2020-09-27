package pl.edu.agh.pawicao.studying_squirrels_api.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtRequest implements Serializable {

  private static final long serialVersionUID = 7839905187546231472L;

  private String email;
  private String password;

}