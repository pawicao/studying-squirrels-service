package pl.edu.agh.pawicao.studying_squirrels_api.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Message;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.MessageRepository;

import java.time.LocalDateTime;

@Service
public class MessageService {

  @Autowired
  MessageRepository messageRepository;

  public Message addMessage(Long senderId, Long receiverId, String text, Long dateInMillis) {
    return messageRepository.addMessage(senderId, receiverId, text, dateInMillis);
  }
}
