package pl.edu.agh.pawicao.studying_squirrels_api.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Message;
import pl.edu.agh.pawicao.studying_squirrels_api.service.api.MessageService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class MessageController {

  @Autowired
  MessageService messageService;

  @PostMapping("/message")
  ResponseEntity<Message> addMessage(
    @RequestParam Long senderId, // TODO: Change to body params
    @RequestParam Long receiverId,
    @RequestParam String text,
    @RequestParam Long dateInMillis
  ) {
    return ResponseEntity.ok(
      messageService.addMessage(senderId, receiverId, text, dateInMillis)
    );
  }
}
