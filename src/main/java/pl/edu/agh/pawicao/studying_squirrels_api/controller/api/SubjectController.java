package pl.edu.agh.pawicao.studying_squirrels_api.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Subject;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Subject.BasicSubjectDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.service.api.SubjectService;
import pl.edu.agh.pawicao.studying_squirrels_api.util.Mapper;

import java.util.List;
import java.util.Map;

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
  ResponseEntity<Subject> createSubject(
    @RequestBody Map<String, String> body
  ) {
    return ResponseEntity.ok(subjectService.createSubject(body.get("name")));
  }

}
