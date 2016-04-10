package co.edu.udistrital.dulcesparamiamor.interfaces;

/**
 * Created by Jeison on 10/04/2016.
 */
public interface IMessageActivityPresenter {
    public void onCreate(IMessageView view);
    public void sendSMS(String phoneNumber, String message);

}
