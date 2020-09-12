package pl.edu.agh.pawicao.studying_squirrels_api.service.auth;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.PersonRepository;

@Service
public class PersonAuthService implements UserDetailsService {

  @Autowired
  private PersonRepository personRepository;

  @Autowired
  private PasswordEncoder bcryptEncoder;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Person person = personRepository.findPersonByEmail(email);
    if (person == null) {
      throw new UsernameNotFoundException("Person not found with email: " + email);
    }
    return new User(
        person.getEmail(),
        person.getPassword(),
        new ArrayList<>()
    );
  }

  public Person save(Person person) {
    person.setPassword(bcryptEncoder.encode(person.getPassword()));
    return personRepository.save(person);
  }
}