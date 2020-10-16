package pl.edu.agh.pawicao.studying_squirrels_api.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Subject;

import java.util.List;

public interface SubjectRepository extends Neo4jRepository<Subject, Long> {

  List<Subject> findAll();

}
