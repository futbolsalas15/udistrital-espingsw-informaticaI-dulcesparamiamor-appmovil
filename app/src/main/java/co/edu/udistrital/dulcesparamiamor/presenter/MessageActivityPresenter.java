package co.edu.udistrital.dulcesparamiamor.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.ReferenceQueue;

import co.edu.udistrital.dulcesparamiamor.interfaces.IMessageActivityPresenter;
import co.edu.udistrital.dulcesparamiamor.interfaces.IMessageView;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Jeison on 10/04/2016.
 */
public class MessageActivityPresenter implements IMessageActivityPresenter{
    private Handler handler;
    private Context ctx;

    public void setParams(RequestParams params) {
        this.params = params;
    }

    private RequestParams params = new RequestParams();

    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String phoneNumber = null;

    @Override
    public void setMessage(String message) {
        this.message = message;
    }
    public String message = null;

    @Override
    public void onCreate(Context ctx , Handler handler) {
        this.ctx = ctx;

        if(handler!=null)
        this.handler = handler;
        else
            this.handler = new Handler();
    }

    @Override
    public void sendSMS() {
        handler.post(new Runnable() {
            public void run() {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                    Toast.makeText(ctx, "SMS Sent.", Toast.LENGTH_LONG).show();
                }
                catch (Exception e) {
                    Toast.makeText(ctx, "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void makeHTTPCallFace() {
        handler.post(new Runnable() {
            public void run() {
                AsyncHttpClient client = new AsyncHttpClient();
                client.get("http://espingsw2016.tk/sendmessage",params, new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            //prgDialog.hide();
                            String str = new String(responseBody, "UTF-8");
                            JSONObject jsonObject = new JSONObject(str);
                            String code = jsonObject.getString("status");
                            if(code.equals("OK")) {
                                Toast.makeText(ctx,
                                        "MSG send",
                                        Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(ctx,
                                        "MSG not was send",
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        // Hide Progress Dialog
                        //prgDialog.hide();
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(ctx,
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(ctx,
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(ctx,
                                    "Device not connected to Internet. HTTP Status code : "
                                            + statusCode, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
            }
        });

    }
}
