package pl.edu.agh.pawicao.studying_squirrels_api.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Attachment;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Homework;

import java.util.List;

public interface HomeworkRepository extends Neo4jRepository<Homework, Long> {

  @Query(
    "MATCH (lesson:Lesson)-[isOf:IS_OF]->(subject:Subject) WHERE ID(lesson) = $lessonId " +
    "CREATE (homework:Homework {deadline: datetime({timezone: 'Europe/Warsaw', epochSeconds: $deadline/1000}), textContent: $textContent})" +
    "<-[has:HAS]-(lesson) " +
    "RETURN lesson, has, homework, isOf, subject"
  )
  Homework setHomework(Long lessonId, Long deadline, String textContent);

  @Query(
    "MATCH (homework:Homework)-[c:CONTAINS]->(a:Attachment) WHERE ID(homework) = $homeworkId " +
    "SET homework = {deadline: datetime({timezone: 'Europe/Warsaw', epochSeconds: $deadline/1000}), textContent: $textContent} " +
    "RETURN homework, a, c"
  )
  Homework editHomework(Long homeworkId, Long deadline, String textContent);

  @Query(
    "MATCH (homework:Homework)<-[has:HAS]-(lesson:Lesson)-[c:CONTAINS]->(a:Attachment) WHERE ID(homework) = $id " +
    "SET homework.done = true, homework.handedIn = datetime({timezone: 'Europe/Warsaw', epochSeconds: $dateInMillis/1000}), " +
    "homework.solution = $solution " +
    "RETURN homework, has, lesson, a, c"
  )
  Homework addHomework(Long id, Long dateInMillis, String solution);

  @Query(
    "MATCH (lesson:Lesson)-[has:HAS]->(homework:Homework)-[c:CONTAINS]->(a:Attachment) WHERE ID(homework) = $id " +
    "SET homework.handedIn = datetime({timezone: 'Europe/Warsaw', epochSeconds: $dateInMillis/1000}), homework.solution = $solution " +
    "RETURN homework, has, lesson, a, c"
  )
  Homework editHomeworkSolution(Long id, String solution, Long dateInMillis);

  @Query(
    "MATCH (attachment:Attachment) WHERE ID(attachment) = $id DETACH DELETE attachment"
  )
  void deleteAttachment(Long id);

  @Query(
    "MATCH (a:Attachment)<-[c:CONTAINS]-(h:Homework)<-[has:HAS]-(l:Lesson)-[isOf:IS_OF]->(s:Subject) " +
    "MATCH (tutor:Person)-[gave:GAVE]->(l)<-[took:TOOK]-(p:Person) WHERE ID(p) = $personId " +
    "RETURN h, has, l, isOf, s, gave, tutor, a, c"
  )
  List<Homework> getReceivedHomeworks(Long personId);

  @Query(
    "MATCH (a:Attachment)<-[c:CONTAINS]-(h:Homework)<-[has:HAS]-(l:Lesson)-[isOf:IS_OF]->(s:Subject) " +
    "MATCH (student:Person)-[took:TOOK]->(l)<-[gave:GAVE]-(p:Person) WHERE ID(p) = $personId " +
    "RETURN h, has, l, isOf, s, student, took, a, c"
  )
  List<Homework> getGivenHomeworks(Long personId);
}
