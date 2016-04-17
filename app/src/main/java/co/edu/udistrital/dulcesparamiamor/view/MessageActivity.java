package co.edu.udistrital.dulcesparamiamor.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.interfaces.IMessageActivityPresenter;
import co.edu.udistrital.dulcesparamiamor.interfaces.IMessageView;
import co.edu.udistrital.dulcesparamiamor.presenter.MessageActivityPresenter;

public class MessageActivity extends AppCompatActivity implements IMessageView {

    private static final int SMS_PERMISSION_GRANTED = 1;
    private IMessageActivityPresenter presenter = new MessageActivityPresenter();

    private Button sendmesagge;

    private EditText txtmessage;
    private EditText txtphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAndRequestPermision();

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





    private void checkAndRequestPermision() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        SMS_PERMISSION_GRANTED);

            }
        }else{
            Log.e("We have permssion", "");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SMS_PERMISSION_GRANTED: {
                Log.e("PERMISSION_GRANT", String.valueOf(grantResults.length));
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.e("READ PERMison GRANTED", "");
                } else {

                    Log.e("NO PERMSISSION ", "");
                }
                return;
            }

        }
    }

}
