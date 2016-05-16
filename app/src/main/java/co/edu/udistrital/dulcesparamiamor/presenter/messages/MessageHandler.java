package co.edu.udistrital.dulcesparamiamor.presenter.messages;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by futbo on 15/05/2016.
 */
public class MessageHandler {

    private List<IMessageSender> messageSenders;

    public MessageHandler() {
        messageSenders = new ArrayList<>();
    }

    public void addMessageSender(IMessageSender msgSender){
        messageSenders.add(msgSender);
    }

    public final void handle(MessageContent msgContent){
        for (IMessageSender msgHandler : messageSenders){
            msgHandler.sendMessage(msgContent);
        }
    }

}
