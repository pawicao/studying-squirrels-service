package pl.edu.agh.pawicao.studying_squirrels_api.controller.semweb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.SemwebRequest;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.SemwebResponse;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb.SemwebRequestProperties;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.semweb.SemwebResponseProperties;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.SemwebEntity;
import pl.edu.agh.pawicao.studying_squirrels_api.service.api.SemwebService;
import pl.edu.agh.pawicao.studying_squirrels_api.util.SemwebRates;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/semweb")
public class SemwebController {

    @Autowired
    private SemwebService semwebService;

    @PostMapping("/extract")
    ResponseEntity<SemwebResponse> extractEntity(
            @RequestBody SemwebRequest requestBody
    ) {
        // initialing response arrays
        List<String> spotlightEntities = new ArrayList<>();
        List<SemwebEntity> semwebEntities = new ArrayList<>();

        // initiating requestProperties and responseProperties for the right return values objects
        SemwebRequestProperties requestProperties = requestBody.getProperties();
        SemwebResponseProperties responseProperties = new SemwebResponseProperties();

        // setting confidence and relatedness rates for initial requests
        if (requestProperties.getConfidenceRate() == null) {
            requestProperties.setConfidenceRate(SemwebRates.INITIAL_RATE);
            responseProperties.setConfidenceRate(SemwebRates.INITIAL_RATE - SemwebRates.DIFF_RATE);
        }
        if (requestProperties.getRelatednessRate() == null) {
            requestProperties.setRelatednessRate(SemwebRates.INITIAL_RATE);
            responseProperties.setRelatednessRate(SemwebRates.INITIAL_RATE - SemwebRates.DIFF_RATE);
        }

        // query DBpediaSpotlight
        if (requestProperties.getSpotlightEntities().isEmpty() || (requestProperties.getRelatednessRate() < SemwebRates.MIN_RATE && !requestProperties.getIsCacheSeeked())) {
            if (requestProperties.getRelatednessRate() < SemwebRates.MIN_RATE) {
                responseProperties.setRelatednessRate(SemwebRates.INITIAL_RATE - SemwebRates.DIFF_RATE);
            }
            do {
                spotlightEntities = semwebService.queryDBpediaSpotlight(requestBody.getText(), requestProperties.getConfidenceRate());
                if (spotlightEntities.isEmpty()) {
                    requestProperties.setConfidenceRate(requestProperties.getConfidenceRate() - SemwebRates.DIFF_RATE);
                    responseProperties.setConfidenceRate(requestProperties.getConfidenceRate() - SemwebRates.DIFF_RATE);
                    if (requestProperties.getConfidenceRate() < SemwebRates.MIN_RATE) {
                        break;
                    }
                }
            } while (spotlightEntities.isEmpty());
            if (spotlightEntities.isEmpty()) {
                return ResponseEntity.ok(new SemwebResponse()); // TODO: Return empty response formatted to show that no new things are there
            }
        }

        // get entities from cache
        if (!requestProperties.getIsCacheSeeked()) {
            System.out.println("semwebService.queryCache()");
            semwebEntities = semwebService.queryCache(spotlightEntities, requestProperties.getRelatednessRate());
        }

        // get entities from dbpedia query
        if (semwebEntities.isEmpty()) {
            System.out.println("semwebService.queryDBpedia()");
            semwebEntities = semwebService.queryDBpedia(spotlightEntities, requestProperties.getRelatednessRate());
        }


        return ResponseEntity.ok(new SemwebResponse());
    }

}
