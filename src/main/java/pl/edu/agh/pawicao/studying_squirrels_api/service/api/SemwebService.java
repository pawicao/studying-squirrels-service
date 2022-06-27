package pl.edu.agh.pawicao.studying_squirrels_api.service.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.SemwebEntity;

import java.util.*;

@Service
public class SemwebService {

    private final static String API_LINK = "https://api.dbpedia-spotlight.org/en/annotate";

    public List<String> queryDBpediaSpotlight(String text, double confidenceRate) {
        List<String> resourceUris = new ArrayList<>();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                API_LINK + "?text=" + text + "&confidence=" + confidenceRate,
                HttpMethod.GET,
                entity,
                JsonNode.class
        );
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
        return null;
    }

    private void updateCache(List<SemwebEntity> semwebEntities) {

    }
}
