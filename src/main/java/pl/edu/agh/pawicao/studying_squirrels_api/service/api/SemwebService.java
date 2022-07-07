package pl.edu.agh.pawicao.studying_squirrels_api.service.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.SemwebEntity;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class SemwebService {

  private static RestTemplate restTemplate = new RestTemplate();

  private static final String SPOTLIGHT_API_LINK = "https://api.dbpedia-spotlight.org/en/annotate";
  private static final String DBPEDIA_API_LINK =
      "https://dbpedia.org/sparql?default-graph-uri=http://dbpedia.org&format=application/sparql-results+json&timeout=30000&signal_void=on&signal_unconnected=on";

  public List<String> queryDBpediaSpotlight(String text, double confidenceRate) {
    List<String> resourceUris = new ArrayList<>();

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    HttpEntity<String> entity = new HttpEntity<>("body", headers);

    ResponseEntity<JsonNode> response =
        restTemplate.exchange(
            SPOTLIGHT_API_LINK + "?text=" + text + "&confidence=" + confidenceRate,
            HttpMethod.GET,
            entity,
            JsonNode.class);

    if (response.getStatusCode() != HttpStatus.OK) {
      return resourceUris;
    }
    try {
      JsonNode resources = Objects.requireNonNull(response.getBody()).get("Resources");
      if (resources == null) {
        return resourceUris;
      }
      for (final JsonNode resource : resources) {
        resourceUris.add(resource.get("@URI").asText());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resourceUris;
  }

  public List<SemwebEntity> queryCache(List<String> spotlightEntities, double relatednessRate) {
    return null;
  }

  public List<SemwebEntity> queryDBpedia(List<String> spotlightEntities, double relatednessRate) {
    List<SemwebEntity> entityResources = new ArrayList<>();

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    HttpEntity<String> entity = new HttpEntity<>(new HttpHeaders());

    String query = "select distinct ?Concept where {[] a ?Concept} LIMIT 100"; // TODO

    String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

    System.out.println(DBPEDIA_API_LINK + "&query=" + encodedQuery);

    ResponseEntity<JsonNode> response =
        restTemplate.exchange(
            DBPEDIA_API_LINK + "&query={query}", HttpMethod.GET, entity, JsonNode.class, query);
    System.out.println("============");
    System.out.println(response.getBody());
    System.out.println(response.getStatusCode());
    System.out.println("============");

    if (response.getStatusCode() != HttpStatus.OK) {
      return entityResources;
    }
    try {
      JsonNode resources = Objects.requireNonNull(response.getBody()).get("Resources");
      if (resources == null) {
        return entityResources;
      }
      // for (final JsonNode resource : resources) {
      // entityResources.add(resource.get("@URI").asText());
      // }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return entityResources;
  }

  private void updateCache(List<SemwebEntity> semwebEntities) {}
}
