package co.edu.udistrital.dulcesparamiamor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import Model.UserProfile;

public class LoginActivity extends AppCompatActivity {
    TextView lblsingup ;
    EditText Email,Password;
    Button button;
    AlertDialog.Builder builder;

    UserProfile userProfile;
    SharedPreferences mPrefs ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        lblsingup = (TextView)findViewById(R.id.lblsingup);

        Email = (EditText) findViewById(R.id.txtemail);
        Password = (EditText) findViewById(R.id.txtpassword);
        button = (Button) findViewById(R.id.button);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
       //Linea valida las preferencias si el usuario ya se ha logueado se obtiene el json del usuario y se redirige al Home.
        Gson gson = new Gson();
        String json = mPrefs.getString("UserProfile", "");
        if(!json.equalsIgnoreCase(""))
        {
            userProfile = gson.fromJson(json, UserProfile.class);
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }
        //Fin Cache del usuario.

        lblsingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Email.getText().toString().equals("") || Password.getText().toString().equals(""))
                {
                 builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Something went wrong");
                    builder.setMessage("Please fill all the fields");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else {
                    BackgroundTask backgroundTask = new BackgroundTask(LoginActivity.this);
                    backgroundTask.execute("login", Email.getText().toString(), Password.getText().toString());
                }

            }
        });
    }
}
