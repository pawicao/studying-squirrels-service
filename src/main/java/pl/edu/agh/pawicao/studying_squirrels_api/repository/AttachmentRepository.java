package pl.edu.agh.pawicao.studying_squirrels_api.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Attachment;

import java.util.Optional;

public interface AttachmentRepository extends Neo4jRepository<Attachment, Long> {
  Optional<Attachment> findById(Long id);
}
