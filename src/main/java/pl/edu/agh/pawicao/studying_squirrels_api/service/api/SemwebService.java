package pl.edu.agh.pawicao.studying_squirrels_api.service.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb.SemwebResponseEntity;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.SemwebEntity;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.SemwebEntityConnection;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.SemwebRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.util.SemwebPair;
import pl.edu.agh.pawicao.studying_squirrels_api.util.SemwebRates;
import pl.edu.agh.pawicao.studying_squirrels_api.util.VarUtils;

import java.text.MessageFormat;
import java.util.*;

@Service
public class SemwebService {

  @Autowired private SemwebRepository semwebRepository;

  private static final RestTemplate restTemplate = new RestTemplate();

  private static final String SPOTLIGHT_API_LINK = "https://api.dbpedia-spotlight.org/en/annotate";

  private static final Map<Integer, String> queriesTemplates =
      Map.of(
          8,
              "select distinct (SAMPLE(?firstNameUnsampled) as ?firstName) ?firstLink "
                  + "(SAMPLE(?secondNameUnsampled) as ?secondName) ?secondLink "
                  + "(SAMPLE(?thirdNameUnsampled) as ?thirdName) ?thirdLink where '{' "
                  + "OPTIONAL '{' <{0}> foaf:name ?firstNameUnsampled . '}' "
                  + "OPTIONAL '{' <{1}> foaf:name ?secondNameUnsampled . '}' "
                  + "OPTIONAL '{' <{2}> foaf:name ?thirdNameUnsampled . '}' "
                  + "<{0}> foaf:isPrimaryTopicOf ?firstLink . "
                  + "<{1}> foaf:isPrimaryTopicOf ?secondLink . "
                  + "<{2}> foaf:isPrimaryTopicOf ?thirdLink . "
                  + "'{' <{1}> ?x <{2}> . '}' "
                  + "UNION "
                  + "'{' <{2}> ?y <{1}> . '}' "
                  + "'{' <{0}> ?xx <{2}> . '}' "
                  + "UNION "
                  + "'{' <{2}> ?yy <{0}> . '}' "
                  + "'{' <{1}> ?xxx <{0}> . '}' "
                  + "UNION "
                  + "'{' <{0}> ?yyy <{1}> . '}' '}' "
                  + "GROUP BY ?firstLink ?secondLink ?thirdLink",
          6,
              "select (SAMPLE(?firstNameUnsampled) as ?firstName) ?firstLink "
                  + "(SAMPLE(?secondNameUnsampled) as ?secondName) ?secondLink where '{' "
                  + "OPTIONAL '{' <{0}> foaf:name ?firstNameUnsampled . '}' "
                  + "OPTIONAL '{' <{1}> foaf:name ?secondNameUnsampled . '}' "
                  + "'{' <{0}> ?x <{1}> . '}' "
                  + "UNION "
                  + "'{' <{1}> ?y <{0}> . '}' "
                  + "<{0}> foaf:isPrimaryTopicOf ?firstLink . "
                  + "<{1}> foaf:isPrimaryTopicOf ?secondLink . '}' "
                  + "GROUP BY ?firstLink ?secondLink",
          4,
              "select (SAMPLE(?firstNameUnsampled) as ?firstName) ?firstLink "
                  + "?middleUri (SAMPLE(?middleNameUnsampled) as ?middleName) ?middleLink "
                  + "(SAMPLE(?secondNameUnsampled) as ?secondName) ?secondLink where '{' "
                  + "OPTIONAL '{' <{0}> foaf:name ?firstNameUnsampled . '}' "
                  + "OPTIONAL '{' <{1}> foaf:name ?secondNameUnsampled . '}' "
                  + "OPTIONAL '{' ?middleUri foaf:name ?middleNameUnsampled . '}' "
                  + "<{0}> foaf:isPrimaryTopicOf ?firstLink . "
                  + "<{1}> foaf:isPrimaryTopicOf ?secondLink . "
                  + "?middleUri foaf:isPrimaryTopicOf ?middleLink . "
                  + "'{' <{0}> ?x ?middleUri . '}' "
                  + "UNION "
                  + "'{' ?middleUri ?y <{0}> . '}' "
                  + "'{' ?middleUri ?xx <{1}> . '}' "
                  + "UNION "
                  + "'{' <{1}> ?yy ?middleUri . '}' '}' "
                  + "GROUP BY ?firstLink ?secondLink ?middleLink ?middleUri",
          2,
              "select DISTINCT (SAMPLE(?firstNameUnsampled) as ?firstName) ?firstLink "
                  + "?middleUri1 (SAMPLE(?middleName1Unsampled) as ?middleName1) ?middleLink1 "
                  + "?middleUri2 (SAMPLE(?middleName2Unsampled) as ?middleName2) ?middleLink2 "
                  + "(SAMPLE(?secondNameUnsampled) as ?secondName) ?secondLink where '{' "
                  + "OPTIONAL '{' <{0}> foaf:name ?firstNameUnsampled . '}' "
                  + "<{0}> foaf:isPrimaryTopicOf ?firstLink . "
                  + "'{' <{0}> ?x ?middleUri1 . '}' "
                  + "UNION "
                  + "'{' ?middleUri1 ?v <{0}> . '}' "
                  + "OPTIONAL '{' ?middleUri1 foaf:name ?middleName1Unsampled . '}' "
                  + "?middleUri1 foaf:isPrimaryTopicOf ?middleLink1 . "
                  + "OPTIONAL '{' ?middleUri2 foaf:name ?middleName2Unsampled . '}' "
                  + "?middleUri2 foaf:isPrimaryTopicOf ?middleLink2 . "
                  + "'{' ?middleUri2 ?x ?middleUri1 . '}' "
                  + "UNION "
                  + "'{' ?middleUri1 ?v ?middleUri2 . '}' "
                  + "'{' <{1}> ?xx ?middleUri2 . '}' "
                  + "UNION "
                  + "'{' ?middleUri2 ?vv <{1}> . '}' "
                  + "OPTIONAL '{' <{1}> foaf:name ?secondNameUnsampled . '}' "
                  + "<{1}> foaf:isPrimaryTopicOf ?secondLink . '}' "
                  + "GROUP BY ?firstLink ?secondLink ?middleLink1 ?middleUri1 ?middleLink2 ?middleUri2");

  public List<String> queryDBpediaSpotlight(String text, double confidenceRate) {
    Set<String> resourceUris = new HashSet<>();

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
      return new ArrayList<>();
    }
    try {
      JsonNode resources = Objects.requireNonNull(response.getBody()).get("Resources");
      if (resources == null) {
        return new ArrayList<>();
      }
      for (final JsonNode resource : resources) {
        resourceUris.add(resource.get("@URI").asText());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ArrayList<>(resourceUris);
  }

  private void handleDBpediaLevels(
      Map<String, List<SemwebPair>> entityLinks,
      QuerySolution soln,
      double relatednessRate,
      String firstEntity,
      String secondEntity,
      String thirdEntity) {
    switch ((int) (relatednessRate * 10)) {
      case 8:
        handleDbpediaZeroLevel(entityLinks, soln, firstEntity, secondEntity, thirdEntity);
        break;
      case 6:
        handleDbpediaFirstLevel(entityLinks, soln, firstEntity, secondEntity);
        break;
      case 4:
        handleDbpediaSecondLevel(entityLinks, soln, firstEntity, secondEntity);
        break;
      default:
        handleDbpediaThirdLevel(entityLinks, soln, firstEntity, secondEntity);
    }
  }

  private void querySingleDBpediaConnection(
      Map<String, List<SemwebPair>> entityLinks,
      double relatednessRate,
      String firstEntity,
      String secondEntity,
      String thirdEntity)
      throws QueryException {
    // JENA operations start
    String queryString =
        "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
            + getQuery(relatednessRate, firstEntity, secondEntity, thirdEntity);
    System.out.println("Executing DBpedia query:");
    System.out.println(queryString);
    Query query = QueryFactory.create(queryString);
    QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
    ResultSet results = qexec.execSelect();
    while (results.hasNext()) {
      QuerySolution soln = results.nextSolution();
      handleDBpediaLevels(
          entityLinks, soln, relatednessRate, firstEntity, secondEntity, thirdEntity);
    }
    qexec.close();
  }

  private List<SemwebResponseEntity> queryDBPediaConnections(
      List<String> spotlightEntities, double relatednessRate) {
    Map<String, List<SemwebPair>> entityLinks = new HashMap<>();
    if (relatednessRate == SemwebRates.INITIAL_RATE) {
      for (int i = 0; i < spotlightEntities.size(); i++) {
        for (int j = i + 1; j < spotlightEntities.size(); j++) {
          for (int k = j + 1; k < spotlightEntities.size(); k++) {
            try {
              querySingleDBpediaConnection(
                  entityLinks,
                  relatednessRate,
                  spotlightEntities.get(i),
                  spotlightEntities.get(j),
                  spotlightEntities.get(k));
            } catch (QueryException ex) {
              System.out.println("EXCEPTION " + relatednessRate);
              // ex.printStackTrace();
              return new ArrayList<>();
            }
          }
        }
      }
    } else {
      for (int i = 0; i < spotlightEntities.size(); i++) {
        for (int j = i + 1; j < spotlightEntities.size(); j++) {
          try {
            querySingleDBpediaConnection(
                entityLinks,
                relatednessRate,
                spotlightEntities.get(i),
                spotlightEntities.get(j),
                null);
          } catch (QueryException ex) {
            return new ArrayList<>();
          }
        }
      }
    }
    Map<String, SemwebResponseEntity> result =
        mapSemwebPairsToEntities(entityLinks, relatednessRate);
    if (result.isEmpty()) {
      List<SemwebResponseEntity> listResult = new ArrayList<>();
      if (relatednessRate >= 0.6) {
        // if no connections found, return only spotlight entities
        System.out.println("No connections found, returning spotlight entities");
        System.out.println("- Analyzing: " + spotlightEntities);
        for (String spotlightEntity : spotlightEntities) {
          String queryString =
              "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                  + getQueryForSpotlightEntities(spotlightEntity);
          Query query = QueryFactory.create(queryString);
          try (QueryExecution qexec =
              QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query)) {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
              QuerySolution soln = results.nextSolution();
              String link = soln.getResource("link").getURI();
              String name = getSemwebName(soln.getLiteral("name"), link);
              listResult.add(new SemwebResponseEntity(spotlightEntity, name, link));
            }
          } catch (QueryException ex) {
            return listResult;
          }
        }
      }
      return listResult;
    }
    updateCache(entityLinks, result);

    List<SemwebResponseEntity> listResult = new ArrayList<>(result.values());
    listResult.sort(Comparator.comparingInt(SemwebResponseEntity::getOccurrences).reversed());
    return listResult;
  }

  public List<SemwebResponseEntity> queryDBpedia(
      List<String> spotlightEntities, double relatednessRate) {
    System.out.println(
        "Sanity check: queryDBpedia() run with the relatednessRate of " + relatednessRate);
    return queryDBPediaConnections(spotlightEntities, relatednessRate);
  }

  private String getQuery(
      double relatednessRate, String firstUri, String secondUri, String thirdUri) {
    int key = (int) (relatednessRate * 10);
    String query = queriesTemplates.get(key);
    if (query == null) {
      return "";
    }
    if (key > 7) {
      return MessageFormat.format(query, firstUri, secondUri, thirdUri);
    }
    return MessageFormat.format(query, firstUri, secondUri);
  }

  private String getQueryForSpotlightEntities(String uri) {
    return MessageFormat.format(
        "SELECT (SAMPLE(?nameUnsampled) as ?name) ?link "
            + "WHERE '{' "
            + "OPTIONAL '{' <{0}> foaf:name ?nameUnsampled . '}' <{0}> foaf:isPrimaryTopicOf ?link . '}' "
            + "GROUP BY ?link",
        uri);
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

  private String getSemwebName(Literal nameLiteral, String link) {
    return nameLiteral == null
        ? VarUtils.getAlternativeNameFromLink(link)
        : VarUtils.removeSuffixIfExists(nameLiteral.toString(), "@en");
  }

  private void handleDbpediaZeroLevel(
      Map<String, List<SemwebPair>> entityLinks,
      QuerySolution soln,
      String firstSpotlightEntity,
      String secondSpotlightEntity,
      String thirdSpotlightEntity) {
    String firstLink = soln.getResource("firstLink").getURI();
    String secondLink = soln.getResource("secondLink").getURI();
    String thirdLink = soln.getResource("thirdLink").getURI();
    String firstName = getSemwebName(soln.getLiteral("firstName"), firstLink);
    String secondName = getSemwebName(soln.getLiteral("secondName"), secondLink);
    String thirdName = getSemwebName(soln.getLiteral("thirdName"), thirdLink);
    System.out.println(firstLink);
    System.out.println(secondLink);
    System.out.println(thirdLink);
    modifyEntityPair(
        entityLinks,
        1,
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
        thirdSpotlightEntity,
        thirdName,
        thirdLink);
    modifyEntityPair(
        entityLinks,
        1,
        secondSpotlightEntity,
        secondName,
        secondLink,
        thirdSpotlightEntity,
        thirdName,
        thirdLink);
  }

  private void handleDbpediaFirstLevel(
      Map<String, List<SemwebPair>> entityLinks,
      QuerySolution soln,
      String firstSpotlightEntity,
      String secondSpotlightEntity) {
    String firstLink = soln.getResource("firstLink").getURI();
    String secondLink = soln.getResource("secondLink").getURI();
    String firstName = getSemwebName(soln.getLiteral("firstName"), firstLink);
    String secondName = getSemwebName(soln.getLiteral("secondName"), secondLink);
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
    String firstLink = soln.getResource("firstLink").getURI();
    String secondLink = soln.getResource("secondLink").getURI();
    String middleLink = soln.getResource("middleLink").getURI();
    String middleUri = soln.getResource("middleUri").getURI();
    String firstName = getSemwebName(soln.getLiteral("firstName"), firstLink);
    String secondName = getSemwebName(soln.getLiteral("secondName"), secondLink);
    String middleName = getSemwebName(soln.getLiteral("middleName"), middleLink);
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
    String firstLink = soln.getResource("firstLink").getURI();
    String middleLink1 = soln.getResource("middleLink1").getURI();
    String middleLink2 = soln.getResource("middleLink2").getURI();
    String secondLink = soln.getResource("secondLink").getURI();
    String middleUri1 = soln.getResource("middleUri1").getURI();
    String middleUri2 = soln.getResource("middleUri2").getURI();
    String firstName = getSemwebName(soln.getLiteral("firstName"), firstLink);
    String middleName1 = getSemwebName(soln.getLiteral("middleName1"), middleLink1);
    String middleName2 = getSemwebName(soln.getLiteral("middleName2"), middleLink2);
    String secondName = getSemwebName(soln.getLiteral("secondName"), secondLink);
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

  private Map<String, SemwebResponseEntity> mapSemwebPairsToEntities(
      Map<String, List<SemwebPair>> entityLinks, double relatednessRate) {
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
    if (relatednessRate < 0.6) {
      result.keySet().removeIf(key -> result.get(key).getOccurrences() < 3);
    }
    return result;
  }

  public Set<SemwebResponseEntity> queryCache(
      List<String> spotlightEntities, double relatednessRate) {
    Set<SemwebResponseEntity> result = new HashSet<>();
    for (int i = 0; i < spotlightEntities.size(); ++i) {
      for (int j = i + 1; j < spotlightEntities.size(); ++j) {
        List<SemwebEntity> cachedPair =
            semwebRepository.findPairsByRelatedness(
                spotlightEntities.get(i),
                spotlightEntities.get(j),
                relatednessRate,
                SemwebRates.NUMBER_OF_CONNECTIONS_DIVIDER);
        if (cachedPair == null || cachedPair.isEmpty()) {
          continue;
        }
        SemwebEntity firstEntity = cachedPair.get(0);
        SemwebEntity secondEntity = cachedPair.get(1);
        SemwebEntityConnection relationshipDetails =
            firstEntity.getRelatedEntities().isEmpty()
                ? firstEntity.getRelatedEntitiesIncoming().get(0)
                : firstEntity.getRelatedEntities().get(0);
        int relatednessScore =
            relationshipDetails.getNumberOfConnections()
                / SemwebRates.NUMBER_OF_CONNECTIONS_DIVIDER
                / relationshipDetails.getShortestDistance()
                * 10;
        result.add(new SemwebResponseEntity(firstEntity, relatednessScore));
        result.add(new SemwebResponseEntity(secondEntity, relatednessScore));
      }
    }
    return result;
  }

  public void updateCache(
      Map<String, List<SemwebPair>> semwebEntities, Map<String, SemwebResponseEntity> occurences) {
    for (List<SemwebPair> semwebPairList : semwebEntities.values()) {
      for (SemwebPair semwebPair : semwebPairList) {
        SemwebResponseEntity first = semwebPair.getFirst();
        SemwebResponseEntity second = semwebPair.getSecond();
        if (occurences.containsKey(first.getUri()) && occurences.containsKey(second.getUri())) {
          List<SemwebEntity> cachedPair =
              semwebRepository.findPairByUris(first.getUri(), second.getUri());
          if (cachedPair == null || cachedPair.isEmpty()) {
            semwebRepository.createPairConnection(
                first.getUri(),
                first.getName(),
                first.getWikipediaUrl(),
                second.getUri(),
                second.getName(),
                second.getWikipediaUrl(),
                semwebPair.getNumberOfConnections(),
                semwebPair.getShortestDistance());
            continue;
          }
          SemwebEntity firstEntity = cachedPair.get(0);
          List<SemwebEntityConnection> relationshipDetails =
              firstEntity.getRelatedEntities().isEmpty()
                  ? firstEntity.getRelatedEntitiesIncoming()
                  : firstEntity.getRelatedEntities();
          int newNumberOfConnections = relationshipDetails.get(0).getNumberOfConnections() + 1;
          int newShortestDistance = relationshipDetails.get(0).getShortestDistance() + 1;
          semwebRepository.updatePairConnection(
              semwebPair.getFirst().getUri(),
              semwebPair.getSecond().getUri(),
              newNumberOfConnections,
              newShortestDistance);
        }
      }
    }
  }
}
