package pl.edu.agh.pawicao.studying_squirrels_api.model.node;

import lombok.Data;
import org.neo4j.ogm.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.PlaceOfResidence;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.TakenLesson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NodeEntity
@Data
public class Person {
  // TODO: Projections zamiast DTO

  @Id
  @GeneratedValue
  private Long id;

  private String email;

  private String password;

  @Property("first_name")
  private String firstName;

  @Property("last_name")
  private String lastName;

  @Property("born")
  private LocalDate dateOfBirth;

  @Property("is_tutor")
  private boolean isTutor;

  @Property("is_student")
  private boolean isStudent;

  private String phone;

  @Property("photo")
  private String photoPath;

  @Relationship(type = "TOOK")
  private List<TakenLesson> takenLessons = new ArrayList<>();

  @Relationship(type = "GAVE")
  private List<TakenLesson> givenLessons = new ArrayList<>();

  @Relationship(type = "OFFERS")
  private List<Subject> offeredSubjects = new ArrayList<>();

  @Relationship(type = "SENT")
  private List<Message> sentMessages = new ArrayList<>();

  @Relationship(type = "RECEIVED")
  private List<Message> receivedMassages = new ArrayList<>();

  @Relationship(type = "LIVES_IN")
  private PlaceOfResidence placeOfResidence;

}