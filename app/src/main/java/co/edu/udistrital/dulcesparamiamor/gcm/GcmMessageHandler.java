package co.edu.udistrital.dulcesparamiamor.gcm;

/**
 * Created by Oscar on 17/04/2016.
 */
    import com.google.android.gms.gcm.GoogleCloudMessaging;
    import com.loopj.android.http.RequestParams;

    import android.app.IntentService;
    import android.content.Intent;
    import android.os.Bundle;
    import android.os.Handler;
    import android.util.Log;
    import android.widget.Toast;

    import co.edu.udistrital.dulcesparamiamor.interfaces.IMessageActivityPresenter;
    import co.edu.udistrital.dulcesparamiamor.interfaces.IMessageView;
    import co.edu.udistrital.dulcesparamiamor.presenter.MessageActivityPresenter;
    import co.edu.udistrital.dulcesparamiamor.presenter.messages.FacebookMessage;
    import co.edu.udistrital.dulcesparamiamor.presenter.messages.MessageContent;
    import co.edu.udistrital.dulcesparamiamor.presenter.messages.MessageHandler;

public class GcmMessageHandler extends IntentService implements IMessageView {

    private IMessageActivityPresenter presenter = new MessageActivityPresenter();
    String mes;
    private Handler handler;
    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }
    private MessageHandler msgHandler;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
        presenter.onCreate(getApplicationContext(), handler);
    }

    private void initMsgHandlers(){
        msgHandler = new MessageHandler();
        msgHandler.addMessageSender(new FacebookMessage(handler));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        mes = extras.getString("title");
        //Envio de Msg de Texto
        presenter.setPhoneNumber("3214045176");
        presenter.setMessage("Hola Mundo!!");
        presenter.sendSMS();
        // Fin Envio Msg de Texto

        MessageContent msgContent = new MessageContent();
        msgContent.setEmail("jeisontriananr14@hotmail.com");
        msgContent.setTextOfMsg("Hola from app! !");
        msgHandler.handle(msgContent);

        //Envio Msg al Face
        /*
        RequestParams params = new RequestParams();
        params.put("token","FHFJF83638464");
        params.put("email","jeisontriananr14@hotmail.com");
        params.put("msg","Hola from app! !");
        presenter.setParams(params);
        presenter.makeHTTPCallFace();
        */
        //Fin Envio Msg al Face

        showToast();
        Log.i("GCM", "Received : (" +messageType+")  "+extras.getString("title"));
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    @Override
    public void showToast(){
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(),mes , Toast.LENGTH_LONG).show();
            }
        });
    }

}