package pl.edu.agh.pawicao.studying_squirrels_api.service.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.jena.query.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb.SemwebResponseEntity;
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

  public List<SemwebResponseEntity> queryDBpedia(
      List<String> spotlightEntities, double relatednessRate) {
    System.out.println(
        "Sanity check: queryDBpedia() run with the relatednessRate of " + relatednessRate);
    Map<String, List<SemwebPair>> entityLinks = new HashMap<>();
    for (int i = 0; i < spotlightEntities.size(); i++) {
      for (int j = i + 1; j < spotlightEntities.size(); j++) {
        // JENA operations start
        String queryString =
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                + getQuery(relatednessRate, spotlightEntities.get(i), spotlightEntities.get(j));
        System.out.println("Executing DBpedia query:");
        System.out.println(queryString);
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec =
            QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
        try {
          ResultSet results = qexec.execSelect();
          while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            if (relatednessRate == 0.8) {
              handleDbpediaFirstLevel(
                  entityLinks, soln, spotlightEntities.get(i), spotlightEntities.get(j));
            } else if (relatednessRate == 0.6) {
              handleDbpediaSecondLevel(
                  entityLinks, soln, spotlightEntities.get(i), spotlightEntities.get(j));
            } else {
              handleDbpediaThirdLevel(
                  entityLinks, soln, spotlightEntities.get(i), spotlightEntities.get(j));
            }
          }
        } catch (QueryException ex) {
          return mapSemwebPairsToEntities(entityLinks);
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
                + "<{1}> foaf:name ?secondNameUnsampled ; foaf:isPrimaryTopicOf ?secondLink . '}' "
                + "GROUP BY ?firstLink ?secondLink ?middleLink1 ?middleUri1 ?middleLink2 ?middleUri2",
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

  private void handleDbpediaThirdLevel(
      Map<String, List<SemwebPair>> entityLinks,
      QuerySolution soln,
      String firstSpotlightEntity,
      String secondSpotlightEntity) {
    String firstName =
        VarUtils.removeSuffixIfExists(soln.getLiteral("firstName").toString(), "@en");
    String firstLink = soln.getResource("firstLink").getURI();
    String middleName1 =
        VarUtils.removeSuffixIfExists(soln.getLiteral("middleName1").toString(), "@en");
    String middleUri1 = soln.getResource("middleUri1").getURI();
    String middleLink1 = soln.getResource("middleLink1").getURI();
    String secondName =
        VarUtils.removeSuffixIfExists(soln.getLiteral("secondName").toString(), "@en");
    String secondLink = soln.getResource("secondLink").getURI();
    String middleName2 =
        VarUtils.removeSuffixIfExists(soln.getLiteral("middleName2").toString(), "@en");
    String middleUri2 = soln.getResource("middleUri2").getURI();
    String middleLink2 = soln.getResource("middleLink2").getURI();
    modifyEntityPair(
        entityLinks,
        3,
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
        middleUri1,
        middleName1,
        middleLink1);
    modifyEntityPair(
        entityLinks,
        2,
        firstSpotlightEntity,
        firstName,
        firstLink,
        middleUri2,
        middleName2,
        middleLink2);
    modifyEntityPair(
        entityLinks,
        2,
        secondSpotlightEntity,
        secondName,
        secondLink,
        middleUri1,
        middleName1,
        middleLink1);
    modifyEntityPair(
        entityLinks,
        1,
        secondSpotlightEntity,
        secondName,
        secondLink,
        middleUri2,
        middleName2,
        middleLink2);
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
      SemwebResponseEntity firstEntity = new SemwebResponseEntity(firstUri, firstName, firstLink);
      SemwebResponseEntity secondEntity =
          new SemwebResponseEntity(secondUri, secondName, secondLink);
      SemwebPair semwebPair = new SemwebPair(firstEntity, secondEntity, shortestDistance, 1);
      List<SemwebPair> semwebPairList = entityLinks.get(firstUri);
      if (semwebPairList == null) {
        semwebPairList = new ArrayList<>();
        semwebPairList.add(semwebPair);
        entityLinks.put(firstUri, semwebPairList);
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

  private List<SemwebResponseEntity> mapSemwebPairsToEntities(
      Map<String, List<SemwebPair>> entityLinks) {
    Map<String, SemwebResponseEntity> result = new HashMap<>();
    for (List<SemwebPair> semwebPairList : entityLinks.values()) {
      for (SemwebPair semwebPair : semwebPairList) {
        SemwebResponseEntity firstPairEntity = semwebPair.getFirst();
        SemwebResponseEntity secondPairEntity = semwebPair.getSecond();
        SemwebResponseEntity firstEntity = result.get(firstPairEntity.getUri());
        SemwebResponseEntity secondEntity = result.get(secondPairEntity.getUri());
        if (firstEntity != null) {
          firstEntity.setOccurrences(firstEntity.getOccurrences() + 1);
        } else {
          result.put(firstPairEntity.getUri(), firstPairEntity);
        }
        if (secondEntity != null) {
          secondEntity.setOccurrences(secondEntity.getOccurrences() + 1);
        } else {
          result.put(secondPairEntity.getUri(), secondPairEntity);
        }
      }
    }
    List<SemwebResponseEntity> resultList = new ArrayList<>(result.values());
    resultList.sort(Comparator.comparingInt(SemwebResponseEntity::getOccurrences).reversed());
    return resultList;
  }

  public List<SemwebResponseEntity> queryCache(
      List<String> spotlightEntities, double relatednessRate) {
    return null;
  }

  private void updateCache(List<SemwebResponseEntity> semwebEntities) {}
}
