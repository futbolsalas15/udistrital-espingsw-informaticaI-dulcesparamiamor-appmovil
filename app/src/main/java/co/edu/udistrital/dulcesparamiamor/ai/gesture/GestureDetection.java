package co.edu.udistrital.dulcesparamiamor.ai.gesture;

/**
 * Created by juliosaldana on 5/12/16.
 */
public class GestureDetection {


    /* Metodo nativo */
    public native String  stringFromJNI();

    static {
        System.loadLibrary("gesture-ai");
    }
}
