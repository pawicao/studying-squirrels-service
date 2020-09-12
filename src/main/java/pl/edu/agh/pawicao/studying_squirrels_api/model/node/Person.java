package pl.edu.agh.pawicao.studying_squirrels_api.model.node;

import org.neo4j.ogm.annotation.*;

@NodeEntity
public class Person {

  @Id
  @GeneratedValue
  private Long id;

  @Property("firstName")
  private String firstName;

  @Property("email")
  private String email;

  @Property("password")
  private String password;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
}