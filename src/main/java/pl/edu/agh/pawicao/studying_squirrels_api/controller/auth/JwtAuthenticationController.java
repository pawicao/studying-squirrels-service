package pl.edu.agh.pawicao.studying_squirrels_api.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.edu.agh.pawicao.studying_squirrels_api.config.auth.JwtTokenUtil;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.ProfileEditRequest;
import pl.edu.agh.pawicao.studying_squirrels_api.model.auth.JwtRequest;
import pl.edu.agh.pawicao.studying_squirrels_api.model.auth.JwtResponse;
import pl.edu.agh.pawicao.studying_squirrels_api.model.auth.RegistrationRequest;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.DetailedPersonAcquaintanceDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.service.auth.PersonAuthService;
import pl.edu.agh.pawicao.studying_squirrels_api.util.Mapper;
import pl.edu.agh.pawicao.studying_squirrels_api.util.exception.ConflictException;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class JwtAuthenticationController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  private PersonAuthService personAuthService;

  @RequestMapping(
    value = "/mailcheck/{email}",
    method = RequestMethod.GET
  )
  public ResponseEntity<?> checkIfEmailExists(@PathVariable String email) {
    return ResponseEntity.ok(personAuthService.checkIfEmailExists(email));
  }

  @RequestMapping(
    value = "/authenticate",
    method = RequestMethod.POST,
    consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<?> createAuthenticationToken(JwtRequest authenticationRequest) throws Exception {
    Long userId = authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
    final String token = jwtTokenUtil.generateToken(authenticationRequest.getEmail());
    return ResponseEntity.ok(new JwtResponse(token, userId));
  }

  @PutMapping("/person")
  public ResponseEntity<DetailedPersonAcquaintanceDTO> editProfile(
    @RequestBody ProfileEditRequest request
  ) throws Exception {
    authenticate(request.getEmail(), request.getOldPassword());
    return ResponseEntity.ok(
      Mapper.map(personAuthService.editProfile(request), DetailedPersonAcquaintanceDTO.class)
    );
  }

  @RequestMapping(
    value = "/register",
    method = RequestMethod.POST,
    consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<?> createPerson(RegistrationRequest person) {
    try {
      Person newPerson = personAuthService.createPerson(person);
      final String token = jwtTokenUtil.generateToken(newPerson.getEmail());
      return ResponseEntity.ok(new JwtResponse(token, newPerson.getId()));
    } catch (ConflictException ex) {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT, ex.getMessage(), ex);
    }
  }

  private Long authenticate(String email, String password) throws Exception {
    try {
      return personAuthService.getIdFromEmail(
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password)).getName()
      );
    } catch (BadCredentialsException e) {
      throw new Exception("INVALID_CREDENTIALS", e);
    }
  }
}