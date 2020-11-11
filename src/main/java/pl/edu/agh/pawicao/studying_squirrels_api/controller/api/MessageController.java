package pl.edu.agh.pawicao.studying_squirrels_api.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.MessageRequest;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Message;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Message.BasicMessageDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person.BasicPersonAcquaintanceDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.service.api.MessageService;
import pl.edu.agh.pawicao.studying_squirrels_api.util.Mapper;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MessageController {

  private static BasicMessageDTO mapToDto(Message message) {
    BasicMessageDTO result = new BasicMessageDTO();
    result.setDate(message.getDate());
    result.setId(message.getId());
    result.setText(message.getText());
    result.setSender(Mapper.map(message.getSender(), BasicPersonAcquaintanceDTO.class));
    result.setReceiver(Mapper.map(message.getReceiver(), BasicPersonAcquaintanceDTO.class));
    return result;
  }

  @Autowired
  MessageService messageService;

  @PostMapping("/message")
  ResponseEntity<BasicMessageDTO> addMessage(
    @RequestBody MessageRequest messageRequest
  ) {
    return ResponseEntity.ok(mapToDto(messageService.addMessage(messageRequest)));
  }

  @GetMapping("/messages")
  ResponseEntity<List<BasicMessageDTO>> getMessages(
    @RequestParam Long myId,
    @RequestParam Long someoneId
  ) {
    List<BasicMessageDTO> result = new ArrayList<>();
    for(Message message : messageService.getMessages(myId, someoneId)) {
      result.add(mapToDto(message));
    }
    return ResponseEntity.ok(result);
  }

  @GetMapping("/allMessages")
  ResponseEntity<List<BasicMessageDTO>> getAllMessages(
    @RequestParam Long id
  ) {
    List<BasicMessageDTO> result = new ArrayList<>();
    for(Message message : messageService.getAllMessages(id)) {
      result.add(mapToDto(message));
    }
    return ResponseEntity.ok(result);
  }
}
