package pl.edu.agh.pawicao.studying_squirrels_api.controller.semweb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.SemwebRequest;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.SemwebResponse;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb.SemwebPropertiesEntity;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb.SemwebRequestProperties;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb.SemwebResponseProperties;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.SemwebEntity;
import pl.edu.agh.pawicao.studying_squirrels_api.service.api.SemwebService;
import pl.edu.agh.pawicao.studying_squirrels_api.util.SemwebRates;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/semweb")
public class SemwebController {

  @Autowired private SemwebService semwebService;

  private List<SemwebEntity> filterEntities(
      List<SemwebEntity> responseEntities, List<SemwebPropertiesEntity> propsEntities) {
    return responseEntities.stream()
        .filter(
            e ->
                propsEntities.stream()
                    .map(SemwebPropertiesEntity::getUri)
                    .anyMatch(uri -> uri.equals(e.getUri())))
        .collect(Collectors.toList());
  }

  @PostMapping("/extract")
  ResponseEntity<SemwebResponse> extractEntities(@RequestBody SemwebRequest requestBody) {
    // initialing response arrays
    List<String> spotlightEntities = new ArrayList<>();
    List<SemwebEntity> semwebEntities = new ArrayList<>();

    // initiating requestProps and responseProps for the right return values objects
    SemwebRequestProperties requestProps = requestBody.getProperties();
    SemwebResponseProperties responseProps = new SemwebResponseProperties();

    // setting confidence and relatedness rates for initial requests
    if (requestProps.getConfidenceRate() == null) {
      requestProps.setConfidenceRate(SemwebRates.INITIAL_RATE);
      responseProps.setConfidenceRate(SemwebRates.INITIAL_RATE - SemwebRates.DIFF_RATE);
    }
    if (requestProps.getRelatednessRate() == null) {
      requestProps.setRelatednessRate(SemwebRates.INITIAL_RATE);
      responseProps.setRelatednessRate(SemwebRates.INITIAL_RATE - SemwebRates.DIFF_RATE);
    }

    // query DBpediaSpotlight
    if (requestProps.getSpotlightEntities().isEmpty()) {
      do {
        spotlightEntities =
            semwebService.queryDBpediaSpotlight(
                requestBody.getText(), requestProps.getConfidenceRate());
        if (spotlightEntities.isEmpty()) {
          requestProps.setConfidenceRate(requestProps.getConfidenceRate() - SemwebRates.DIFF_RATE);
          responseProps.setConfidenceRate(requestProps.getConfidenceRate() - SemwebRates.DIFF_RATE);
          if (requestProps.getConfidenceRate() < SemwebRates.MIN_RATE) {
            break;
          }
        }
      } while (spotlightEntities.isEmpty());
      responseProps.setSpotlightEntities(spotlightEntities);
      if (spotlightEntities.isEmpty()) {
        return ResponseEntity.ok(new SemwebResponse(semwebEntities, responseProps));
      }
    }

    // get entities from cache
    if (!requestProps.getIsCacheSeeked()) {
      System.out.println("semwebService.queryCache()");
      semwebEntities =
          filterEntities(
              semwebService.queryCache(spotlightEntities, requestProps.getRelatednessRate()),
              requestProps.getExtractedEntities());
      responseProps.setIsCacheSeeked(true);
      // TODO: get relatednessScore for each and sort them accordingly
    }

    // get entities from dbpedia query
    if (semwebEntities.isEmpty()) {
      System.out.println("semwebService.queryDBpedia()");
      semwebEntities =
          filterEntities(
              semwebService.queryDBpedia(spotlightEntities, requestProps.getRelatednessRate()),
              requestProps.getExtractedEntities());
      responseProps.setIsCacheSeeked(false);
      responseProps.setRelatednessRate(requestProps.getRelatednessRate() - SemwebRates.DIFF_RATE);
      if (semwebEntities.isEmpty()) {
        if (responseProps.getRelatednessRate() < SemwebRates.MIN_RATE) {
          responseProps.setSpotlightEntities(new ArrayList<>());
        }
        return extractEntities(
            new SemwebRequest(
                requestBody.getText(),
                new SemwebRequestProperties(
                    responseProps.getIsCacheSeeked(),
                    responseProps.getConfidenceRate(),
                    responseProps.getRelatednessRate(),
                    SemwebPropertiesEntity.mapToPropertyEntities(semwebEntities),
                    responseProps.getSpotlightEntities())));
      } else {
        System.out.println("UPDATE THE CACHE"); // TODO: only here update the cache
      }
    }

    return ResponseEntity.ok(new SemwebResponse(semwebEntities, responseProps));
  }
}
