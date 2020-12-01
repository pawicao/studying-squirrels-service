package pl.edu.agh.pawicao.studying_squirrels_api.model.relationship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.neo4j.ogm.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Lesson;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;

@RelationshipEntity(type = "GAVE")
@Data
public class GivenLesson {

  @Id
  @GeneratedValue
  private Long id;

  @JsonIgnoreProperties({
    "takenLessons", "givenLessons", "password", "dateOfBirth", "student", "studentRating",
    "studentRatingsGiven", "offeredSubjects", "sentMessages", "receivedMessages", "friendshipsInitiated",
    "friendshipsReceived", "placeOfResidence"
  })
  @StartNode
  private Person tutor;

  @EndNode
  private Lesson lesson;

  private Double tutorRating;

  private String tutorRatingDescription;

}
