package pl.edu.agh.pawicao.studying_squirrels_api.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.RawSubjectRequest;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Subject;

import java.util.List;

public interface SubjectRepository extends Neo4jRepository<Subject, Long> {

  List<Subject> findAll();
  Subject findByName(String name);

  @Query(
    "WITH $subjects as p " +
    "FOREACH (row in p | MERGE (s:Subject {name: row.name, icon: row.icon})) " +
    "WITH p MATCH (sub:Subject) RETURN sub"
  )
  List<Subject> loadInitialSubjects(List<RawSubjectRequest> subjects);
}
