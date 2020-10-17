package pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.neo4j.annotation.QueryResult;

public interface BasicPersonProjection {

  Long getId();

  String getFirstName();

  //@Value("#{target.student}")
  //boolean isStudent();
  //@Value("#{target.tutor}")
  //boolean isTutor();
  String getPhotoPath();

}
