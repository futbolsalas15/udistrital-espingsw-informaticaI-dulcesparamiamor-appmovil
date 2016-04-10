package co.edu.udistrital.dulcesparamiamor.presenter;

import android.app.Activity;
import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

import co.edu.udistrital.dulcesparamiamor.interfaces.IMessageActivityPresenter;
import co.edu.udistrital.dulcesparamiamor.interfaces.IMessageView;

/**
 * Created by Jeison on 10/04/2016.
 */
public class MessageActivityPresenter implements IMessageActivityPresenter{
    private IMessageView view;

    @Override
    public void onCreate(IMessageView view) {
        this.view = view;
    }

    @Override
    public void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText((Activity)view, "SMS Sent.", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText((Activity)view, "SMS faild, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
