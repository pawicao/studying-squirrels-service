package pl.edu.agh.pawicao.studying_squirrels_api.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.City;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.CityRepository;

import java.util.List;

@Service
public class CityService {
  @Autowired
  private CityRepository cityRepository;

  public List<City> getAllCities() {
    return cityRepository.findAll();
  }
}
