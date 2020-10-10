package pl.edu.agh.pawicao.studying_squirrels_api.model.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Acquaintance;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Offer;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.PlaceOfResidence;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.TakenLesson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NodeEntity
@Getter @Setter
public class Person {

  @Id
  @GeneratedValue
  private Long id;

  private String email;

  private String password;

  private String firstName;

  private String lastName;

  @Property("born")
  private LocalDate dateOfBirth;

  @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
  private boolean tutor;

  private Double tutorRating;
  private Integer tutorRatingsGiven;

  @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
  private boolean student;

  private Double studentRating;
  private Integer studentRatingsGiven;

  private String phone;

  @Property("photo")
  private String photoPath;

  @Relationship(type = "TOOK")
  private List<TakenLesson> takenLessons = new ArrayList<>();

  @Relationship(type = "GAVE")
  private List<TakenLesson> givenLessons = new ArrayList<>();

  @Relationship(type = "OFFERS")
  private List<Offer> offeredSubjects = new ArrayList<>();

  @Relationship(type = "SENT")
  private List<Message> sentMessages = new ArrayList<>();

  @Relationship(type = "RECEIVED", direction = "INCOMING")
  private List<Message> receivedMessages = new ArrayList<>();

  @Relationship(type="IS_FRIEND")
  private List<Acquaintance> friendshipsInitiated = new ArrayList<>();

  @Relationship(type="IS_FRIEND", direction = "INCOMING")
  private List<Acquaintance> friendshipsReceived = new ArrayList<>();

  @Relationship(type = "LIVES_IN")
  private PlaceOfResidence placeOfResidence;

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