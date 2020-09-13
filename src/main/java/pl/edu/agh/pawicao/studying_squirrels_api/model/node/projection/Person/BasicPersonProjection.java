package pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person;

public interface BasicPersonProjection {

  Long getId();
  String getFirstName();
  boolean getIsStudent();
  boolean getIsTutor();
  String getPhotoPath();

}
