package pl.edu.agh.pawicao.studying_squirrels_api.model.api;

import lombok.Data;

@Data
public class ProfileEditRequest {
  private Long id;
  private String email;
  private String oldPassword;
  private String newPassword;
  private String cityName;
  private String postalCode;
  private String street;
  private String phone;
}
