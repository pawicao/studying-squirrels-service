package pl.edu.agh.pawicao.studying_squirrels_api.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Subject;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.BasicSubjectDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.service.api.SubjectService;
import pl.edu.agh.pawicao.studying_squirrels_api.util.exception.Mapper;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SubjectController {

  @Autowired
  private SubjectService subjectService;

  @GetMapping("/subjects")
  ResponseEntity<List<BasicSubjectDTO>> getAllSubjects() {
    return ResponseEntity.ok(Mapper.mapAll(subjectService.getAllSubjects(), BasicSubjectDTO.class));
  }

  @PostMapping("/subject")
  ResponseEntity<Subject> addSubject(
      @RequestParam String name // TODO: Change to body params
  ) {
    return ResponseEntity.ok(subjectService.addSubject(name));
  }

}
