package co.edu.udistrital.dulcesparamiamor.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.opencv.core.Mat;
import org.opencv.objdetect.Objdetect;

import java.util.HashMap;

import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.utils.ServiceClient;

/**
 * Created by JulioS on 03/04/2016.
 */
public class ValidarAmorClient extends AsyncTask<Mat, Integer, Response> {

    private ServiceClient serviceClient;
    public ValidarAmorClient(Context context){
            serviceClient  = new ServiceClient(context.getString(R.string.validar_amor_url),context.getString(R.string.services_namespace) ,context.getString(R.string.validar_amor_method) );

    }


    private String matImageToString(Mat image) {
        int cols = image.cols();
        int rows = image.rows();
        int elemSize = (int) image.elemSize();
        byte[] data = new byte[cols * rows * elemSize];
        image.get(0, 0, data);
        String dataString = new String(Base64.encode(data, Base64.DEFAULT));
        return dataString;
    }

    @Override
    protected Response doInBackground(Mat... params) {
        //TODO call object
        SoapObject response;
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("token", "12345678");
        //properties.put("foto1", matImageToString(params[0]));
        properties.put("foto1", "afdasfasdfasdf");
        properties.put("foto2", "");
        //response = serviceClient.request(properties);
        //Log.e("Validar", response.getProperty(0).toString() + response.getProperty(1).toString());
        return new Response();
    }
}
