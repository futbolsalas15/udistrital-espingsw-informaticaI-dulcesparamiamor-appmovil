package co.edu.udistrital.dulcesparamiamor.view;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.gcm.GCMClient;
import co.edu.udistrital.dulcesparamiamor.gcm.GCMClientID;
import co.edu.udistrital.dulcesparamiamor.gcm.IGCMClient;

public class GCMActivity extends Activity {

    Button btnRegId;
    EditText etRegId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcm);

        btnRegId = (Button) findViewById(R.id.btnGetRegId);
        etRegId = (EditText) findViewById(R.id.etRegId);
        btnRegId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IGCMClient igcmClient = new GCMClient();
                igcmClient.getGCMRegId(getResources().getString(R.string.gcm_SenderId),getApplicationContext());
                etRegId.setText(GCMClientID.createGCMClientID("").getGcmRegId());

            }
        });
    }

}
