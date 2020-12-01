package pl.edu.agh.pawicao.studying_squirrels_api;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import pl.edu.agh.pawicao.studying_squirrels_api.config.storage.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class StudyingSquirrelsApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(StudyingSquirrelsApiApplication.class, args);
  }
  // TODO: Import first data || LOAD CSV WITH HEADERS FROM 'file:///---.csv' AS row MERGE (s:Subject {name: row.name, icon: row.icon}) return count(s)
  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

}
