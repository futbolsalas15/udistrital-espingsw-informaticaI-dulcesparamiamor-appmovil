package co.edu.udistrital.dulcesparamiamor.presenter.messages;

import android.os.Handler;

/**
 * Created by futbo on 15/05/2016.
 */
public interface IMessageSender {

    public void sendMessage(MessageContent msgContent);
    public enum STATUS{ERROR, ON_PROCESS, OK};
    public STATUS getSendStatus();
    public String getErrorMsg();

}
