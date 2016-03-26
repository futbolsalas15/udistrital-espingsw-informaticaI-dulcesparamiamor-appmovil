package co.edu.udistrital.dulcesparamiamor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {
Button buttonnext;

    EditText Name ;
    EditText Email;
    EditText Password;
    EditText PasswordConfirm;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        buttonnext = (Button)findViewById(R.id.buttonnext);


        buttonnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name =  (EditText)findViewById(R.id.txtname);
                Email = (EditText)findViewById(R.id.txtemail);
                Password = (EditText)findViewById(R.id.txtpassword);
                PasswordConfirm = (EditText)findViewById(R.id.txtpasswordconfirm);

                if(Name.getText().toString().equals("") || Email.getText().toString().equals("")
                        || Password.getText().toString().equals("")  || PasswordConfirm.getText().toString().equals("") )
                {
                    builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle(RegisterActivity.this.getString(R.string.somethingwentwrong));
                    builder.setMessage(RegisterActivity.this.getString(R.string.pleasefillallthefields));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Password.setText("");
                            PasswordConfirm.setText("");
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else if ( !Password.getText().toString().equals(PasswordConfirm.getText().toString()) )
                {
                    builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle(RegisterActivity.this.getString(R.string.somethingwentwrong));
                    builder.setMessage(RegisterActivity.this.getString(R.string.confirmpasswordnotequal));
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

                    Intent intent = new Intent(RegisterActivity.this, RegisterLoverActivity.class);
                    intent.putExtra("name", Name.getText());
                    intent.putExtra("email",Email.getText());
                    intent.putExtra("password",PasswordConfirm.getText());
                    startActivity(intent);
                    //startActivity(new Intent(RegisterActivity.this, RegisterLoverActivity.class));
                }
            }
        });
    }
}
