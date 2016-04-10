package co.edu.udistrital.dulcesparamiamor.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.interfaces.IMessageActivityPresenter;
import co.edu.udistrital.dulcesparamiamor.interfaces.IMessageView;
import co.edu.udistrital.dulcesparamiamor.presenter.MessageActivityPresenter;

public class MessageActivity extends AppCompatActivity implements IMessageView {

    private IMessageActivityPresenter presenter = new MessageActivityPresenter();

    private Button sendmesagge;

    private EditText txtmessage;
    private EditText txtphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        presenter.onCreate(this);
        sendmesagge = (Button) findViewById(R.id.btnsend);
        sendmesagge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtmessage = (EditText)findViewById(R.id.txtmessage);
                txtphone = (EditText)findViewById(R.id.txtphone);
                presenter.sendSMS(txtphone.getText().toString(),txtmessage.getText().toString());
            }
        });
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
