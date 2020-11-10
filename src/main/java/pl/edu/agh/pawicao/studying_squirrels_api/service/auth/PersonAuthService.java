package pl.edu.agh.pawicao.studying_squirrels_api.service.auth;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.ProfileEditRequest;
import pl.edu.agh.pawicao.studying_squirrels_api.model.auth.RegistrationRequest;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.City;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.PersonCredentialsProjection;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.PlaceOfResidence;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.CityRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.PersonRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.PlaceOfResidenceRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.util.DateUtils;
import pl.edu.agh.pawicao.studying_squirrels_api.util.exception.ConflictException;

@Service
public class PersonAuthService implements UserDetailsService {

  @Autowired
  private PersonRepository personRepository;

  @Autowired
  private CityRepository cityRepository;

  @Autowired
  private PlaceOfResidenceRepository placeOfResidenceRepository;

  @Autowired
  private PasswordEncoder bcryptEncoder;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    PersonCredentialsProjection personCredentials = personRepository.findPersonByEmail(email);
    if (personCredentials == null) {
      throw new UsernameNotFoundException("Person not found with email: " + email);
    }
    return new User(
      personCredentials.getEmail(),
      personCredentials.getPassword(),
      new ArrayList<>()
    );
  }

  public boolean checkIfEmailExists(String email) {
    return personRepository.existsByEmail(email);
  }

  public Person createPerson(RegistrationRequest req) {
    if (checkIfEmailExists(req.getEmail())) {
      throw new ConflictException("Email");
    }
    Person person = personRepository.addPerson(
      req.getEmail(),
      bcryptEncoder.encode(req.getPassword()),
      req.getFirstName(),
      req.getLastName(),
      req.getPhone(),
      req.isStudent(),
      req.isTutor(),
      Long.parseLong(req.getDateOfBirth())
    );
    Optional<City> optionalCity = cityRepository.findByName(req.getCityName());
    City city;
    if(!optionalCity.isPresent()) {
      city = new City();
      city.setName(req.getCityName());
      cityRepository.save(city);
    }
    else {
      city = optionalCity.get();
    }
    PlaceOfResidence placeOfResidence = new PlaceOfResidence();
    placeOfResidence.setPerson(person);
    placeOfResidence.setCity(city);
    placeOfResidence.setPostalCode(req.getPostalCode());
    placeOfResidence.setStreet(req.getStreet());
    placeOfResidenceRepository.save(placeOfResidence);
    return person;
  }

  public Person editProfile(ProfileEditRequest request) {
    Person person = personRepository.findById(request.getId()).get();
    if(request.getNewPassword() != null && !request.getNewPassword().isEmpty()) person.setPassword(
      bcryptEncoder.encode(request.getNewPassword())
    );
    PlaceOfResidence placeOfResidence = person.getPlaceOfResidence();
    placeOfResidence.setStreet(request.getStreet());
    placeOfResidence.setPostalCode(request.getPostalCode());
    Optional<City> city = cityRepository.findByName(request.getCityName());
    if(city.isPresent())
      placeOfResidence.setCity(city.get());
    else {
      City newCity = new City();
      newCity.setName(request.getCityName());
      cityRepository.save(newCity);
      placeOfResidence.setCity(newCity);
    }
    person.setPhone(request.getPhone());
    person.setPlaceOfResidence(placeOfResidence);
    placeOfResidenceRepository.save(placeOfResidence);
    return personRepository.save(person);
  }
}