package pl.edu.agh.pawicao.studying_squirrels_api.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Homework;

public interface HomeworkRepository extends Neo4jRepository<Homework, Long> {
  @Query(
    "MATCH (lesson:Lesson)-[isOf:IS_OF]->(subject:Subject) WHERE ID(lesson) = $lessonId " +
    "CREATE (homework:Homework {deadline: datetime({timezone: 'Europe/Warsaw', epochSeconds: $deadline/1000}), textContent: $textContent})" +
    "<-[has:HAS]-(lesson) " +
    "RETURN lesson, has, homework, isOf, subject"
  )
  Homework setHomework(Long lessonId, Long deadline, String textContent);

  @Query(
    "MATCH (homework:Homework) WHERE ID(homework) = $homeworkId " +
    "SET homework = {deadline: datetime({timezone: 'Europe/Warsaw', epochSeconds: $deadline/1000}), textContent: $textContent} " +
    "RETURN homework"
  )
  Homework editHomework(Long homeworkId, Long deadline, String textContent);

  @Query(
    "MATCH (homework:Homework)<-[has:HAS]-(lesson:Lesson) WHERE ID(homework) = $id " +
    "SET homework.done = true, homework.handedIn = datetime({timezone: 'Europe/Warsaw', epochSeconds: $dateInMillis/1000}), " +
    "homework.solution = $solution " +
    "RETURN homework, has, lesson"
  )
  Homework addHomework(Long id, Long dateInMillis, String solution);

  @Query(
    "MATCH (lesson:Lesson)-[has:HAS]->(homework:Homework) WHERE ID(homework) = $id " +
    "SET homework.handedIn = datetime({timezone: 'Europe/Warsaw', epochSeconds: $dateInMillis/1000}), homework.solution = $solution " +
    "RETURN homework, has, lesson"
  )
  Homework editHomeworkSolution(Long id, String solution, Long dateInMillis);

  @Query(
    "MATCH (attachment:Attachment) WHERE ID(attachment) = $id DETACH DELETE attachment"
  )
  void deleteAttachment(Long id);
}
