package co.edu.udistrital.dulcesparamiamor.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import co.edu.udistrital.dulcesparamiamor.view.HomeActivity;
import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.Model.UserProfile;

/**
 * Created by Jeison on 21/03/2016.
 */
public class BackgroundTask extends AsyncTask<String,Void,String> {
   String login_url = "http://webappjasontiw.azurewebsites.net/login.php";
    SharedPreferences mPrefs;


    Context ctx;
    Activity activity;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;

    public BackgroundTask(Context ctx){
        //mPrefs =  ctx.getSharedPreferences("UserProfile",ctx.MODE_WORLD_READABLE);
        mPrefs =   PreferenceManager.getDefaultSharedPreferences(ctx);
        this.ctx = ctx;
        activity = (Activity)ctx;

    }
    @Override
    protected void onPreExecute() {
        builder = new AlertDialog.Builder(activity);
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setTitle(activity.getString(R.string.pleasewait));
        progressDialog.setMessage(activity.getString(R.string.connectiontoserver));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();


        //super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        String method = params[0];

        if(method.equals("login"))
        {
        try {
                URL url = new URL(login_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String email = params[1];
                String password = params[2];

                String data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"+
                        URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                bufferedWriter.write(data);

                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line ="";
                while((line = bufferedReader.readLine())!=null){
                    stringBuilder.append(line+"\n");

                }
                httpURLConnection.disconnect();
                //Thread.sleep(5000);
                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }// catch (InterruptedException e) {
             //   e.printStackTrace();
           // }


        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String json) {

        try {
            progressDialog.dismiss();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("server_response");

            JSONObject JO = jsonArray.getJSONObject(0);
            String code = JO.getString("code");
            String message = JO.getString("message");
            if(code.equals("login_true"))
            {
            //showDialog(activity.getString(R.string.logintittlesuccess),activity.getString(R.string.loginsuccess),code);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.putString("UserProfile", JO.getJSONArray("object").getJSONObject(0).toString());
                prefsEditor.commit();

                Intent intent = new Intent (activity,HomeActivity.class);
                activity.startActivity(intent);
            }
            else if (code.equals("login_false"))
            {
                showDialog(activity.getString(R.string.logintittlefailed),activity.getString(R.string.loginfailed),code);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //super.onPostExecute(json);
    }

    public void showDialog(String tittle,  String message ,String code)
    {
        builder.setTitle(tittle);
        if (code.equals("login_true") || code.equals("login_false"))
        {
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    //activity.finish();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }



    }

}
