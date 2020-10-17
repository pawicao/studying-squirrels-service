package pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Person;

import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Message;

import java.util.List;

public interface PersonChatProjection extends PersonAcquaintancesProjection {

  List<Message> getReceivedMessages();

  List<Message> getSentMessages();

}
