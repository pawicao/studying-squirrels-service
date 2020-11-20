package pl.edu.agh.pawicao.studying_squirrels_api.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.City;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Subject.BasicSubjectDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.service.api.CityService;
import pl.edu.agh.pawicao.studying_squirrels_api.util.Mapper;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CityController {

  @Autowired
  private CityService cityService;

  @GetMapping("/cities")
  ResponseEntity<List<City>> getAllCities() {
    return ResponseEntity.ok(cityService.getAllCities());
  }

}
