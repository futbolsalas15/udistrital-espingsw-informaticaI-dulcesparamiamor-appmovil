package co.edu.udistrital.dulcesparamiamor.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import co.edu.udistrital.dulcesparamiamor.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }



    public void loadImagefromGallery(View view)
    {
        startActivity(new Intent(HomeActivity.this, UploadActivity.class));

    }

    public void startValidation(View view)
    {
        startActivity(new Intent(HomeActivity.this, CameraActivity.class));

    }
}
