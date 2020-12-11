package pl.edu.agh.pawicao.studying_squirrels_api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.RawSubjectRequest;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Subject;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.SubjectRepository;

import java.util.List;
@RestController
@RequestMapping("/")
public class CloudTempController {

  @Autowired
  SubjectRepository subjectRepository;

  @GetMapping("/")
  String cloudHello() {
    return "Studying Squirrels Service";
  }

  @GetMapping("/admin/loadInitialSubjects")
  List<Subject> loadInitialSubjects() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    List<RawSubjectRequest> subjectRequestList =
      objectMapper.readValue(initialSubjects, new TypeReference<>() {
      });
    return subjectRepository.loadInitialSubjects(subjectRequestList);
  }

  private String initialSubjects = "[" +
                                   "{\"name\":\"Maths\",\"icon\":\"calculator-variant\"}," +
                                   "{\"name\":\"Greek\",\"icon\":\"web\"}," +
                                   "{\"name\":\"English\",\"icon\":\"web\"}," +
                                   "{\"name\":\"German\",\"icon\":\"web\"}," +
                                   "{\"name\":\"Spanish\",\"icon\":\"web\"}," +
                                   "{\"name\":\"Polish\",\"icon\":\"web\"}," +
                                   "{\"name\":\"Portuguese\",\"icon\":\"web\"}," +
                                   "{\"name\":\"Czech\",\"icon\":\"web\"}," +
                                   "{\"name\":\"Slovak\",\"icon\":\"web\"}," +
                                   "{\"name\":\"Slovenian\",\"icon\":\"web\"}," +
                                   "{\"name\":\"Piano\",\"icon\":\"piano\"},{" +
                                   "\"name\":\"Saxophone\",\"icon\":\"saxophone\"}," +
                                   "{\"name\":\"Guitar\",\"icon\":\"guitar-acoustic\"}," +
                                   "{\"name\":\"Drums\",\"icon\":\"music-note\"}," +
                                   "{\"name\":\"Vocals\",\"icon\":\"microphone-variant\"}," +
                                   "{\"name\":\"Biology\",\"icon\":\"leaf\"}," +
                                   "{\"name\":\"Flute\",\"icon\":\"music-note\"}," +
                                   "{\"name\":\"Computer Science\",\"icon\":\"laptop\"}," +
                                   "{\"name\":\"Physics\",\"icon\":\"atom\"}," +
                                   "{\"name\":\"Chemistry\",\"icon\":\"chemical-weapon\"}," +
                                   "{\"name\":\"Blacksmithing\",\"icon\":\"anvil\"}," +
                                   "{\"name\":\"Carpentry\",\"icon\":\"hand-saw\"}," +
                                   "{\"name\":\"Pottery\",\"icon\":\"pot\"}," +
                                   "{\"name\":\"Violin\",\"icon\":\"violin\"}," +
                                   "{\"name\":\"Sailing\",\"icon\":\"sail-boat\"}," +
                                   "{\"name\":\"Geography\",\"icon\":\"map\"}," +
                                   "{\"name\":\"Latin\",\"icon\":\"web\"}" +
                                   "]";
}