package pl.edu.agh.pawicao.studying_squirrels_api.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Subject;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.SubjectRepository;

import java.util.List;

@Service
public class SubjectService {

  @Autowired
  private SubjectRepository subjectRepository;

  public List<Subject> getAllSubjects() {
    return subjectRepository.findAll();
  }

  public Subject addSubject(String name) {
    Subject subject = new Subject();
    subject.setName(name);
    subject.setIcon("book");
    return subjectRepository.save(subject);
  }
}
