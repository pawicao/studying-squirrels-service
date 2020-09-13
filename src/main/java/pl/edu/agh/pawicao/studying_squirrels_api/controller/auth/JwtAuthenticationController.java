package pl.edu.agh.pawicao.studying_squirrels_api.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.config.auth.JwtTokenUtil;
import pl.edu.agh.pawicao.studying_squirrels_api.model.auth.JwtRequest;
import pl.edu.agh.pawicao.studying_squirrels_api.model.auth.JwtResponse;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Person;
import pl.edu.agh.pawicao.studying_squirrels_api.service.auth.PersonAuthService;

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
    authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
    final UserDetails userDetails = personAuthService.loadUserByUsername(authenticationRequest.getEmail());
    final String token = jwtTokenUtil.generateToken(userDetails);
    return ResponseEntity.ok(new JwtResponse(token));
  }

  @RequestMapping(
      value = "/register",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<?> savePerson(Person person) {
    return ResponseEntity.ok(personAuthService.save(person));
  }

  private void authenticate(String email, String password) throws Exception {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    } catch (DisabledException e) {
      throw new Exception("USER_DISABLED", e);
    } catch (BadCredentialsException e) {
      throw new Exception("INVALID_CREDENTIALS", e);
    }
  }
}