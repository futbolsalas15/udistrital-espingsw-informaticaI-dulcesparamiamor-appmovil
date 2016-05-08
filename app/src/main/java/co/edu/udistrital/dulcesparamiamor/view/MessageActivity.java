package co.edu.udistrital.dulcesparamiamor.view;

import android.os.Handler;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.interfaces.IMessageActivityPresenter;
import co.edu.udistrital.dulcesparamiamor.interfaces.IMessageView;
import co.edu.udistrital.dulcesparamiamor.presenter.MessageActivityPresenter;

import cz.msebera.android.httpclient.Header;

public class MessageActivity extends AppCompatActivity implements IMessageView {

    private IMessageActivityPresenter presenter = new MessageActivityPresenter();
    private Handler handler;
    private Button sendmesagge;
    private Button btnsendfase;
    private EditText txtmessage;
    private EditText txtphone;
    private String message;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        handler = new Handler();
        presenter.onCreate(this, handler);
        sendmesagge = (Button) findViewById(R.id.btnsend);
        sendmesagge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtmessage = (EditText)findViewById(R.id.txtmessage);
                txtphone = (EditText)findViewById(R.id.txtphone);

                presenter.setPhoneNumber(txtphone.getText().toString());
                presenter.setMessage(txtmessage.getText().toString());
                presenter.sendSMS();
            }
        });


        btnsendfase = (Button) findViewById(R.id.btnsendfase);
        btnsendfase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams params = new RequestParams();
                params.put("token","FHFJF83638464");
                params.put("email","jeisontriananr14@hotmail.com");
                params.put("msg","Hola from app v2! !");
                presenter.setParams(params);
                presenter.makeHTTPCallFace();
            }
        });


    }

    @Override
    public void showToast() {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
