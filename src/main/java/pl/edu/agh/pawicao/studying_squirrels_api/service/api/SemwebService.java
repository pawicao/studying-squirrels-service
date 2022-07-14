package pl.edu.agh.pawicao.studying_squirrels_api.service.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.jena.query.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.SemwebEntity;
import pl.edu.agh.pawicao.studying_squirrels_api.util.SemwebPair;
import pl.edu.agh.pawicao.studying_squirrels_api.util.VarUtils;

import java.text.MessageFormat;
import java.util.*;

@Service
public class SemwebService {

  private static final RestTemplate restTemplate = new RestTemplate();

  private static final String SPOTLIGHT_API_LINK = "https://api.dbpedia-spotlight.org/en/annotate";

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

  public Set<SemwebEntity> queryDBpedia(List<String> spotlightEntities, double relatednessRate) {
    Map<String, List<SemwebPair>> entityLinks = new HashMap<>();
    for (int i = 0; i < spotlightEntities.size(); i++) {
      for (int j = i + 1; j < spotlightEntities.size(); j++) {
        // JENA operations start
        String queryString =
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                + getQuery(relatednessRate, spotlightEntities.get(i), spotlightEntities.get(j));
        System.out.println(queryString);
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec =
            QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
        try {
          ResultSet results = qexec.execSelect();
          while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            // highest relatednessrate
            if (relatednessRate == 0.8) {
              handleDbpediaFirstLevel(
                  entityLinks, soln, spotlightEntities.get(i), spotlightEntities.get(j));
            } else {
              handleDbpediaSecondLevel(
                  entityLinks, soln, spotlightEntities.get(i), spotlightEntities.get(j));
            }
          }
        } finally {
          qexec.close();
        }
      }
    }
    // TODO: updateCache here actually
    return mapSemwebPairsToEntities(entityLinks);
  }

  private String getQuery(double relatednessRate, String firstUri, String secondUri) {
    switch ((int) (relatednessRate * 10)) {
      case 8:
        return MessageFormat.format(
            "select (SAMPLE(?firstNameUnsampled) as ?firstName) ?firstLink "
                + "(SAMPLE(?secondNameUnsampled) as ?secondName) ?secondLink where '{' "
                + "<{0}> foaf:name ?firstNameUnsampled ; foaf:isPrimaryTopicOf ?firstLink ; ?x <{1}> . "
                + "<{1}> foaf:name ?secondNameUnsampled ; foaf:isPrimaryTopicOf ?secondLink . '}' "
                + "GROUP BY ?firstLink ?secondLink",
            firstUri, secondUri);
      case 6:
        return MessageFormat.format(
            "select (SAMPLE(?firstNameUnsampled) as ?firstName) ?firstLink "
                + "?middleUri (SAMPLE(?middleNameUnsampled) as ?middleName) ?middleLink "
                + "(SAMPLE(?secondNameUnsampled) as ?secondName) ?secondLink where '{' "
                + "<{0}> foaf:name ?firstNameUnsampled ; foaf:isPrimaryTopicOf ?firstLink ; ?x ?middleUri . "
                + "?middleUri foaf:name ?middleNameUnsampled ; foaf:isPrimaryTopicOf ?middleLink ; ?y <{1}> . "
                + "<{1}> foaf:name ?secondNameUnsampled ; foaf:isPrimaryTopicOf ?secondLink . '}' "
                + "GROUP BY ?firstLink ?secondLink ?middleLink ?middleUri",
            firstUri, secondUri);
      case 4:
        return MessageFormat.format(
            "select DISTINCT (SAMPLE(?firstNameUnsampled) as ?firstName) ?firstLink "
                + "?middleUri1 (SAMPLE(?middleName1Unsampled) as ?middleName1) ?middleLink1 "
                + "?middleUri2 (SAMPLE(?middleName2Unsampled) as ?middleName2) ?middleLink2 "
                + "(SAMPLE(?secondNameUnsampled) as ?secondName) ?secondLink where '{' "
                + "<{0}> foaf:name ?firstNameUnsampled ; foaf:isPrimaryTopicOf ?firstLink . "
                + "'{' <{0}> ?x ?middleUri1 . '}' "
                + "UNION "
                + "'{' ?middleUri1 ?v <{0}> . '}' "
                + "?middleUri1 foaf:name ?middleName1Unsampled ; foaf:isPrimaryTopicOf ?middleLink1 ; ?y ?middleUri2 . "
                + "?middleUri2 foaf:name ?middleName2Unsampled ; foaf:isPrimaryTopicOf ?middleLink2 . "
                + "'{' <{1}> ?x ?middleUri2 . '}' "
                + "UNION "
                + "'{' ?middleUri2 ?v <{1}> . '}' "
                + "<{1}> foaf:name ?secondNameUnsampled ; foaf:isPrimaryTopicOf ?secondLink . '}'",
            firstUri, secondUri);
      default:
        return "";
    }
  }

  private SemwebPair findExistingSemwebPair(
      Map<String, List<SemwebPair>> semwebPairsMap, String firstUri, String secondUri) {
    SemwebPair result = null;
    if (semwebPairsMap.containsKey(firstUri)) {
      result = findExistingSemwebPairInList(semwebPairsMap.get(firstUri), secondUri);
    }
    if (result == null && semwebPairsMap.containsKey(secondUri)) {
      result = findExistingSemwebPairInList(semwebPairsMap.get(secondUri), firstUri);
    }
    return result;
  }

  private SemwebPair findExistingSemwebPairInList(
      List<SemwebPair> semwebPairList, String secondUri) {
    for (SemwebPair semwebPair : semwebPairList) {
      if (Objects.equals(semwebPair.getSecond().getUri(), secondUri)) {
        return semwebPair;
      }
    }
    return null;
  }

  private void updateCache(List<SemwebEntity> semwebEntities) {}

  private void handleDbpediaFirstLevel(
      Map<String, List<SemwebPair>> entityLinks,
      QuerySolution soln,
      String firstSpotlightEntity,
      String secondSpotlightEntity) {
    String firstName =
        VarUtils.removeSuffixIfExists(soln.getLiteral("firstName").toString(), "@en");
    String firstLink = soln.getResource("firstLink").getURI();
    String secondName =
        VarUtils.removeSuffixIfExists(soln.getLiteral("secondName").toString(), "@en");
    String secondLink = soln.getResource("secondLink").getURI();
    modifyEntityPair(
        entityLinks,
        1,
        firstSpotlightEntity,
        firstName,
        firstLink,
        secondSpotlightEntity,
        secondName,
        secondLink);
  }

  private void handleDbpediaSecondLevel(
      Map<String, List<SemwebPair>> entityLinks,
      QuerySolution soln,
      String firstSpotlightEntity,
      String secondSpotlightEntity) {
    String firstName =
        VarUtils.removeSuffixIfExists(soln.getLiteral("firstName").toString(), "@en");
    String firstLink = soln.getResource("firstLink").getURI();
    String middleName =
        VarUtils.removeSuffixIfExists(soln.getLiteral("middleName").toString(), "@en");
    String middleUri = soln.getResource("middleUri").getURI();
    String middleLink = soln.getResource("middleLink").getURI();
    String secondName =
        VarUtils.removeSuffixIfExists(soln.getLiteral("secondName").toString(), "@en");
    String secondLink = soln.getResource("secondLink").getURI();
    modifyEntityPair(
        entityLinks,
        2,
        firstSpotlightEntity,
        firstName,
        firstLink,
        secondSpotlightEntity,
        secondName,
        secondLink);
    modifyEntityPair(
        entityLinks,
        1,
        firstSpotlightEntity,
        firstName,
        firstLink,
        middleUri,
        middleName,
        middleLink);
    modifyEntityPair(
        entityLinks,
        1,
        secondSpotlightEntity,
        secondName,
        secondLink,
        middleUri,
        middleName,
        middleLink);
  }

  private void modifyEntityPair(
      Map<String, List<SemwebPair>> entityLinks,
      int shortestDistance,
      String firstUri,
      String firstName,
      String firstLink,
      String secondUri,
      String secondName,
      String secondLink) {
    SemwebPair pairOfEntities = findExistingSemwebPair(entityLinks, firstUri, secondUri);
    if (pairOfEntities == null) {
      System.out.println("No pair found");
      SemwebEntity firstEntity = new SemwebEntity(firstUri, firstName, firstLink);
      SemwebEntity secondEntity = new SemwebEntity(secondUri, secondName, secondLink);
      System.out.println(firstEntity);
      System.out.println(secondEntity);
      SemwebPair semwebPair = new SemwebPair(firstEntity, secondEntity, shortestDistance, 1);
      List<SemwebPair> semwebPairList = entityLinks.get(firstUri);
      if (semwebPairList == null) {
        System.out.println("No pairlist");
        semwebPairList = new ArrayList<>();
        semwebPairList.add(semwebPair);
        entityLinks.put(firstUri, semwebPairList);
        System.out.println("Entity links now");
        System.out.println(entityLinks);
      } else {
        semwebPairList.add(semwebPair);
      }
    } else {
      if (pairOfEntities.getShortestDistance() > shortestDistance) {
        pairOfEntities.setShortestDistance(shortestDistance);
      }
      pairOfEntities.setNumberOfConnections(pairOfEntities.getNumberOfConnections() + 1);
    }
  }

  private Set<SemwebEntity> mapSemwebPairsToEntities(Map<String, List<SemwebPair>> entityLinks) {
    System.out.println(entityLinks);
    Set<SemwebEntity> result = new HashSet<>();
    System.out.println("MAKARENA");
    System.out.println(entityLinks);
    for (List<SemwebPair> semwebPairList : entityLinks.values()) {
      for (SemwebPair semwebPair : semwebPairList) {
        SemwebEntity firstEntity = semwebPair.getFirst();
        SemwebEntity secondEntity = semwebPair.getSecond();
        System.out.println(firstEntity.getUri());
        System.out.println(secondEntity.getUri());
        // first.setOccurrences(first.getOccurrences() + 1);
        // second.setOccurrences(second.getOccurrences() + 1);
        result.add(firstEntity);
        result.add(secondEntity);
      }
    }
    return result;
  }
}
