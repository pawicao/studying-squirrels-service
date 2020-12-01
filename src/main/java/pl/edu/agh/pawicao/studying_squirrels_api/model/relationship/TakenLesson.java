package pl.edu.agh.pawicao.studying_squirrels_api.model.relationship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.neo4j.ogm.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Lesson;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;

@RelationshipEntity(type = "TOOK")
@Data
public class TakenLesson {

  @Id
  @GeneratedValue
  private Long id;

  @JsonIgnoreProperties({
    "takenLessons", "givenLessons", "password", "dateOfBirth", "tutor", "tutorRating",
    "tutorRatingsGiven", "offeredSubjects", "sentMessages", "receivedMessages", "friendshipsInitiated",
    "friendshipsReceived", "placeOfResidence"
  })
  @StartNode
  private Person student;

  @EndNode
  private Lesson lesson;

  private Double studentRating;

  private String studentRatingDescription;

}
