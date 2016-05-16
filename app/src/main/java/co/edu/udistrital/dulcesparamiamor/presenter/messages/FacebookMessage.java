package co.edu.udistrital.dulcesparamiamor.presenter.messages;

import android.os.Handler;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by futbo on 15/05/2016.
 */
public class FacebookMessage implements IMessageSender{

    private final String TOKEN = "FHFJF83638464";
    private final String RS_URL = "http://espingsw2016.tk/sendmessage";
    private STATUS status;
    private String errorMsg;
    private Handler handler;

    public FacebookMessage(Handler handler) {
        this.handler = handler;
        this.errorMsg = null;
    }

    @Override
    public void sendMessage(MessageContent msgContent) {

        final RequestParams params = new RequestParams();
        params.put("token", TOKEN);
        params.put("email", msgContent.getEmail());
        params.put("msg", msgContent.getTextOfMsg());

        status = STATUS.ON_PROCESS;

        handler.post(new Runnable() {
             public void run() {
                 AsyncHttpClient client = new AsyncHttpClient();
                 client.get(RS_URL, params, new AsyncHttpResponseHandler() {

                     @Override
                     public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                         try {
                             String str = new String(responseBody, "UTF-8");
                             JSONObject jsonObject = new JSONObject(str);
                             String code = jsonObject.getString("status");
                             status = (code.equals("OK")) ? STATUS.OK : STATUS.ERROR;
                             if(jsonObject.has("msg")){
                                 errorMsg = jsonObject.getString("msg");
                             }
                         } catch (Exception e) {
                             status = STATUS.ERROR;
                             errorMsg = e.getMessage();
                         }
                     }

                     @Override
                     public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                         status = STATUS.ERROR;
                         switch (statusCode){
                             case 404: errorMsg = "Requested resource not found"; break;
                             case 500: errorMsg = "Something went wrong at server end"; break;
                             default:  errorMsg = "Device not connected to Internet. HTTP Status code : "+statusCode; break;
                         }
                     }
                 });
             }
         });


    }

    @Override
    public STATUS getSendStatus() {
        return this.status;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }
}
