package pl.edu.agh.pawicao.studying_squirrels_api;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StudyingSquirrelsApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(StudyingSquirrelsApiApplication.class, args);
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

}
