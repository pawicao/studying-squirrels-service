package pl.edu.agh.pawicao.studying_squirrels_api.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.MessageRequest;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Message;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.MessageRepository;

import java.util.List;

@Service
public class MessageService {

  @Autowired
  MessageRepository messageRepository;

  public Message addMessage(MessageRequest messageRequest) {
    return messageRepository.addMessage(
      messageRequest.getSenderId(),
      messageRequest.getReceiverId(),
      messageRequest.getText(),
      messageRequest.getDateInMillis()
    );
  }

  public List<Message> getMessages(Long myId, Long someoneId) {
    return messageRepository.getMessages(myId, someoneId);
  }

  public List<Message> getAllMessages(Long id) {
    return messageRepository.getAllMessages(id);
  }
}
