package co.edu.udistrital.dulcesparamiamor.interfaces;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.loopj.android.http.RequestParams;

/**
 * Created by Jeison on 10/04/2016.
 */
public interface IMessageActivityPresenter {

    public void onCreate(Context ctx , Handler handler);
    public void sendSMS();
    public void sendEMAIL();
    public String phoneNumber = null;
    public void setPhoneNumber(String phoneNumber);
    public String message = null;
    public void setMessage(String message);
    public String email = null;
    public void setEmail(String email);
    public void setParams(RequestParams params);
    public void makeHTTPCallFace();
}
