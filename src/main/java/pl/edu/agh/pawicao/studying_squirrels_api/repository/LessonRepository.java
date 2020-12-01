package pl.edu.agh.pawicao.studying_squirrels_api.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Homework;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Lesson;

import java.util.List;

public interface LessonRepository extends Neo4jRepository<Lesson, Long> {
  @Query(
    "MATCH (student:Person), (city:City)<-[tutorPlace:LIVES_IN]-(tutor:Person)-[offer:OFFERS]->(subject:Subject) " +
    "WHERE ID(student) = $studentId AND ID(offer) = $offerId " +
    "CREATE (student)-[took:TOOK]->(lesson:Lesson {studentDescription: $studentDescription, confirmed: false, " +
    "tutorDescription: '',canceled: false,date: datetime({timezone: 'Europe/Warsaw', epochSeconds: $dateInMillis/1000})})<-[gave:GAVE]-(tutor) " +
    "CREATE (city)<-[wasIn:WAS_IN {street: tutorPlace.street, postalCode: tutorPlace.postalCode}]" +
    "-(lesson)-[isOf:IS_OF]->(subject) " +
    "RETURN student, tutor, offer, subject, lesson, took, gave, isOf, city, wasIn"
  )
  Lesson requestLesson(Long offerId, Long studentId, Long dateInMillis, String studentDescription);

  @Query(
    "MATCH (student:Person)-[took:TOOK]->(lesson:Lesson)<-[gave:GAVE]-(tutor:Person) " +
    "WHERE ID(student) = $personId " +
    "AND (lesson.date < datetime({timezone: 'Europe/Warsaw', epochSeconds: $dateInMillis/1000}) OR lesson.canceled = true) " +
    "MATCH (lesson)-[isOf:IS_OF]->(subject:Subject) " +
    "OPTIONAL MATCH (homework:Homework)<-[has:HAS]-(lesson) " +
    "RETURN student, took, lesson, gave, tutor, isOf, subject, homework, has"
  )
  List<Lesson> findAllPastLessonsForStudent(Long personId, Long dateInMillis);

  @Query(
    "MATCH (student:Person)-[took:TOOK]->(lesson:Lesson)<-[gave:GAVE]-(tutor:Person) " +
    "WHERE ID(student) = $personId " +
    "AND lesson.date > datetime({timezone: 'Europe/Warsaw', epochSeconds: $dateInMillis/1000}) AND NOT lesson.canceled = true " +
    "MATCH (lesson)-[isOf:IS_OF]->(subject:Subject) " +
    "OPTIONAL MATCH (homework:Homework)<-[has:HAS]-(lesson) " +
    "RETURN student, took, lesson, gave, tutor, isOf, subject, homework, has"
  )
  List<Lesson> findAllFutureLessonsForStudent(Long personId, Long dateInMillis);

  @Query(
    "MATCH (student:Person)-[took:TOOK]->(lesson:Lesson)<-[gave:GAVE]-(tutor:Person) " +
    "WHERE ID(tutor) = $personId " +
    "AND (lesson.date < datetime({timezone: 'Europe/Warsaw', epochSeconds: $dateInMillis/1000}) OR lesson.canceled = true) " +
    "MATCH (lesson)-[isOf:IS_OF]->(subject:Subject) " +
    "OPTIONAL MATCH (homework:Homework)<-[has:HAS]-(lesson) " +
    "RETURN student, took, lesson, gave, tutor, isOf, subject, homework, has"
  )
  List<Lesson> findAllPastLessonsForTutor(Long personId, Long dateInMillis);

  @Query(
    "MATCH (student:Person)-[took:TOOK]->(lesson:Lesson)<-[gave:GAVE]-(tutor:Person) " +
    "WHERE ID(tutor) = $personId " +
    "AND lesson.date > datetime({timezone: 'Europe/Warsaw', epochSeconds: $dateInMillis/1000}) AND NOT lesson.canceled = true " +
    "MATCH (lesson)-[isOf:IS_OF]->(subject:Subject) " +
    "OPTIONAL MATCH (homework:Homework)<-[has:HAS]-(lesson) " +
    "RETURN student, took, lesson, gave, tutor, isOf, subject, homework, has"
  )
  List<Lesson> findAllFutureLessonsForTutor(Long personId, Long dateInMillis);

  @Query(
    "MATCH (tutor:Person)-[gave:GAVE]->(lesson:Lesson)<-[took:TOOK]-(student:Person) WHERE ID(lesson) = $lessonId " +
    "SET gave.tutorRating = $rating, gave.tutorRatingDescription = $ratingDescription, " +
    "tutor.tutorRating = (tutor.tutorRatingsGiven * tutor.tutorRating + $rating)/(tutor.tutorRatingsGiven + 1), " +
    "tutor.tutorRatingsGiven = tutor.tutorRatingsGiven + 1 " +
    "RETURN lesson, gave, tutor"
  )
  Lesson setTutorRating(Long lessonId, Double rating, String ratingDescription);

  @Query(
    "MATCH (tutor:Person)-[gave:GAVE]->(lesson:Lesson)<-[took:TOOK]-(student:Person) WHERE ID(lesson) = $lessonId " +
    "SET took.studentRating = $rating, took.studentRatingDescription = $ratingDescription, " +
    "student.studentRating = (student.studentRatingsGiven * student.studentRating + $rating)/(student.studentRatingsGiven + 1), " +
    "student.studentRatingsGiven = student.studentRatingsGiven + 1 " +
    "RETURN lesson, took, student"
  )
  Lesson setStudentRating(Long lessonId, Double rating, String ratingDescription);

  @Query(
    "MATCH (tutor:Person)-[gave:GAVE]->(lesson:Lesson)<-[took:TOOK]-(student:Person) WHERE ID(lesson) = $lessonId " +
    "SET tutor.tutorRating = (tutor.tutorRatingsGiven * tutor.tutorRating - gave.tutorRating + $rating)/tutor.tutorRatingsGiven, " +
    "gave.tutorRating = $rating, gave.tutorRatingDescription = $ratingDescription " +
    "RETURN lesson, gave, tutor"
  )
  Lesson alterTutorRating(Long lessonId, Double rating, String ratingDescription);

  @Query(
    "MATCH (tutor:Person)-[gave:GAVE]->(lesson:Lesson)<-[took:TOOK]-(student:Person) WHERE ID(lesson) = $lessonId " +
    "SET student.studentRating = (student.studentRatingsGiven * student.studentRating - took.studentRating + $rating)/student.studentRatingsGiven, " +
    "took.studentRating = $rating, took.studentRatingDescription = $ratingDescription " +
    "RETURN lesson, took, student"
  )
  Lesson alterStudentRating(Long lessonId, Double rating, String ratingDescription);

}
