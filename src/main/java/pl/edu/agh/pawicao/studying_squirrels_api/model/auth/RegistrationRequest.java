package pl.edu.agh.pawicao.studying_squirrels_api.model.auth;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class RegistrationRequest {
  private String firstName;
  private String lastName;
  private String dateOfBirth;
  private String email;
  private String password;
  private String cityName;
  private String postalCode;
  private String street;
  private String phone;
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private boolean student;
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private boolean tutor;

  public boolean isTutor() {
    return tutor;
  }
  public void setTutor(boolean tutor) {
    this.tutor = tutor;
  }
  public boolean isStudent() {
    return student;
  }
  public void setStudent(boolean student) {
    this.student = student;
  }
}
