package pl.edu.agh.pawicao.studying_squirrels_api.controller.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.OfferEditRequest;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.OfferRequest;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.Offer;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.projection.Offer.OfferDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.service.api.OfferService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class OfferController {

  @Autowired
  private OfferService offerService;

  @PostMapping("/offer")
  ResponseEntity<OfferDTO> addOffer(
    @RequestBody OfferRequest requestBody
  ) {
    return ResponseEntity.ok(OfferDTO.convertToDTO(offerService.createOffer(requestBody)));
  }

  @PutMapping("/offer")
  ResponseEntity<OfferDTO> editOffer(
    @RequestBody OfferEditRequest requestBody
  ) {
    return ResponseEntity.ok(OfferDTO.convertToDTO(offerService.editOffer(requestBody)));
  }

  @GetMapping("/offers")
  ResponseEntity<List<OfferDTO>> getOffers(
    @RequestParam Long tutorId
  ) {
    List<Offer> offers = offerService.getOffers(tutorId);
    List<OfferDTO> result = offers.stream().map(OfferDTO::convertToDTO).collect(Collectors.toList());
    return ResponseEntity.ok(result);
  }

  @DeleteMapping("/offer/{offerId}")
  ResponseEntity<Long> deleteOffer(
    @PathVariable Long offerId
  ) {
    return ResponseEntity.ok(
      offerService.deleteOffer(offerId)
    );
  }

}
