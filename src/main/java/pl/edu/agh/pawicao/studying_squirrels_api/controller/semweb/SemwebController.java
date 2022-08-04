package pl.edu.agh.pawicao.studying_squirrels_api.controller.semweb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.SemwebRequest;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.SemwebResponse;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb.SemwebPropertiesEntity;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb.SemwebRequestProperties;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb.SemwebResponseEntity;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb.SemwebResponseProperties;
import pl.edu.agh.pawicao.studying_squirrels_api.service.api.SemwebService;
import pl.edu.agh.pawicao.studying_squirrels_api.util.SemwebRates;
import pl.edu.agh.pawicao.studying_squirrels_api.util.VarUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/semweb")
public class SemwebController {

  @Autowired private SemwebService semwebService;

  private List<SemwebResponseEntity> filterEntities(
      List<SemwebResponseEntity> responseEntities, List<SemwebPropertiesEntity> propsEntities) {
    Set<String> propsEntitiesURIs =
        propsEntities.stream().map(SemwebPropertiesEntity::getUri).collect(Collectors.toSet());

    return responseEntities.stream()
        .filter(e -> !propsEntitiesURIs.contains(e.getUri()))
        .collect(Collectors.toList());
  }

  private List<SemwebResponseEntity> filterEntities(
      Set<SemwebResponseEntity> responseEntities, List<SemwebPropertiesEntity> propsEntities) {
    Set<String> propsEntitiesURIs =
        propsEntities.stream().map(SemwebPropertiesEntity::getUri).collect(Collectors.toSet());

    return responseEntities.stream()
        .filter(e -> !propsEntitiesURIs.contains(e.getUri()))
        .collect(Collectors.toList());
  }
  /*
  @GetMapping("/debug")
  ResponseEntity<Boolean> debug() {
    Map<String, List<SemwebPair>> debug = new HashMap<>();
    SemwebResponseEntity firstEntity =
        new SemwebResponseEntity(
            "http://dbpedia.org/resource/Leonardo_da_Vinci",
            "Leonardo da Vinci",
            "http://en.wikipedia.org/wiki/Leonardo_da_Vinci");
    SemwebResponseEntity secondEntity =
        new SemwebResponseEntity(
            "http://dbpedia.org/resource/Michelangelo",
            "Michelangelo",
            "http://en.wikipedia.org/wiki/Michelangelo");
    SemwebPair semwebPair = new SemwebPair(firstEntity, secondEntity, 1, 10);
    List<SemwebPair> debugList = new ArrayList<>();
    debugList.add(semwebPair);
    debug.put("makarena", debugList);
    semwebService.updateCache(debug);
    return ResponseEntity.ok(true);
  }

  @GetMapping("/debug2")
  ResponseEntity<List<SemwebResponseEntity>> debug2() {
    List<String> spotlightDebugEntities = new ArrayList<>();
    spotlightDebugEntities.add("http://dbpedia.org/resource/Leonardo_da_Vinci");
    spotlightDebugEntities.add("http://dbpedia.org/resource/Michelangelo");
    List<SemwebResponseEntity> result =
        filterEntities(semwebService.queryCache(spotlightDebugEntities, 0.2), new ArrayList<>());
    result.sort(Comparator.comparingInt(SemwebResponseEntity::getOccurrences).reversed());
    return ResponseEntity.ok(result);
  }*/

  @PostMapping("/extract")
  ResponseEntity<SemwebResponse> extractEntities(@RequestBody SemwebRequest requestBody) {
    System.out.println("[Start of the extraction]");
    // initialing response arrays
    List<String> spotlightEntities = new ArrayList<>();
    List<SemwebResponseEntity> semwebEntities = new ArrayList<>();

    // initiating requestProps and responseProps for the right return values objects
    SemwebRequestProperties requestProps = requestBody.getProperties();
    SemwebResponseProperties responseProps = new SemwebResponseProperties();

    // setting confidence and relatedness rates for initial requests
    if (requestProps.getConfidenceRate() == null) {
      requestProps.setConfidenceRate(SemwebRates.INITIAL_RATE);
      System.out.println(
          "No confidenceRate provided, setting to initialRate: "
              + requestProps.getConfidenceRate());
      responseProps.setConfidenceRate(
          VarUtils.round(SemwebRates.INITIAL_RATE - SemwebRates.DIFF_RATE));
      System.out.println("Setting response confidenceRate to " + responseProps.getConfidenceRate());
    } else {
      responseProps.setConfidenceRate(
          VarUtils.round(requestProps.getConfidenceRate() - SemwebRates.DIFF_RATE));
      if (requestProps.getConfidenceRate() < SemwebRates.MIN_SPOTLIGHT_RATE
          && requestProps.getRelatednessRate() == null) {
        return ResponseEntity.ok(new SemwebResponse(semwebEntities, responseProps));
      }
    }
    if (requestProps.getRelatednessRate() == null) {
      requestProps.setRelatednessRate(SemwebRates.INITIAL_RATE);
      System.out.println(
          "No relatednessRate provided, setting to initialRate: "
              + requestProps.getRelatednessRate());
      responseProps.setRelatednessRate(
          VarUtils.round(SemwebRates.INITIAL_RATE - SemwebRates.DIFF_RATE));
      System.out.println(
          "Setting response relatednessRate to " + responseProps.getRelatednessRate());
    }

    if (requestProps.getConfidenceRate() < SemwebRates.MIN_SPOTLIGHT_RATE
        && requestProps.getRelatednessRate() < SemwebRates.MIN_DBPEDIA_RATE) {
      return ResponseEntity.ok(new SemwebResponse(semwebEntities, responseProps));
    }

    // query DBpediaSpotlight
    if (requestProps.getSpotlightEntities().isEmpty()) {
      System.out.println("No spotlightEntities in props, getting entities from Spotlight");
      do {
        System.out.println(
            "Running spotlightEntities look with confidenceRate of "
                + requestProps.getConfidenceRate());
        spotlightEntities =
            semwebService.queryDBpediaSpotlight(
                requestBody.getText(), requestProps.getConfidenceRate());
        if (spotlightEntities.isEmpty() || spotlightEntities.size() < 2) {
          requestProps.setConfidenceRate(
              VarUtils.round(requestProps.getConfidenceRate() - SemwebRates.DIFF_RATE));
          responseProps.setConfidenceRate(
              VarUtils.round(responseProps.getConfidenceRate() - SemwebRates.DIFF_RATE));
          System.out.println(
              "Not enough entities found in Spotlight, setting the confidenceRate to: "
                  + requestProps.getConfidenceRate());
          if (requestProps.getConfidenceRate() < SemwebRates.MIN_SPOTLIGHT_RATE) {
            break;
          }
        }
      } while (spotlightEntities.isEmpty() || spotlightEntities.size() < 2);
      responseProps.setSpotlightEntities(spotlightEntities);
      if (spotlightEntities.isEmpty() || spotlightEntities.size() < 2) {
        System.out.println("Not enough entities found in Spotlight, returning nothing :(");
        return ResponseEntity.ok(new SemwebResponse(semwebEntities, responseProps));
      }
    } else {
      spotlightEntities = requestProps.getSpotlightEntities();
      responseProps.setConfidenceRate(requestProps.getConfidenceRate());
      responseProps.setSpotlightEntities(spotlightEntities);
    }
    System.out.println(spotlightEntities.size() + " spotlightEntities found. Moving on");

    // get entities from cache
    if (!requestProps.getIsCacheSeeked()) {
      System.out.println("semwebService.queryCache()");
      semwebEntities =
          filterEntities(
              semwebService.queryCache(spotlightEntities, requestProps.getRelatednessRate()),
              requestProps.getExtractedEntities());
      responseProps.setIsCacheSeeked(true);
      semwebEntities.sort(Comparator.comparingInt(SemwebResponseEntity::getOccurrences).reversed());
    }

    // get entities from dbpedia query
    if (semwebEntities.isEmpty()) {
      System.out.println(
          "Starting querying from DBpedia with the relatedness of "
              + requestProps.getRelatednessRate());
      semwebEntities =
          filterEntities(
              semwebService.queryDBpedia(spotlightEntities, requestProps.getRelatednessRate()),
              requestProps.getExtractedEntities());
      responseProps.setIsCacheSeeked(false);
      responseProps.setRelatednessRate(
          VarUtils.round(requestProps.getRelatednessRate() - SemwebRates.DIFF_RATE));
      System.out.println(
          "Found and filtered " + semwebEntities.size() + " semwebEntities from DBpedia");
      if (semwebEntities.isEmpty()) {
        if (responseProps.getRelatednessRate() < SemwebRates.MIN_DBPEDIA_RATE) {
          responseProps.setSpotlightEntities(new ArrayList<>());
          responseProps.setRelatednessRate(null);
        }
        System.out.println("Recurrently running the process with the following parameters:");
        System.out.println("- isCacheSeeked: " + responseProps.getIsCacheSeeked());
        System.out.println("- confidenceRate: " + responseProps.getConfidenceRate());
        System.out.println("- relatednessRate: " + responseProps.getRelatednessRate());
        System.out.println("- spotlightEntities");
        System.out.println(responseProps.getSpotlightEntities());
        return extractEntities(
            new SemwebRequest(
                requestBody.getText(),
                new SemwebRequestProperties(
                    responseProps.getIsCacheSeeked(),
                    responseProps.getConfidenceRate(),
                    responseProps.getRelatednessRate(),
                    SemwebPropertiesEntity.mapToPropertyEntities(semwebEntities),
                    responseProps.getSpotlightEntities())));
      }
    }

    return ResponseEntity.ok(new SemwebResponse(semwebEntities, responseProps));
  }
}
