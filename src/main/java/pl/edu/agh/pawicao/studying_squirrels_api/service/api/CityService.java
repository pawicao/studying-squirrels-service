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
    List<City> cities = cityRepository.findAll();
    cities.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
    return cities;
  }
}
