package co.edu.udistrital.dulcesparamiamor.gcm;

/**
 * Created by Oscar on 17/04/2016.
 */
public class GCMClientID {

    private  static GCMClientID gcmClientID;
    private String gcmRegId;

    private GCMClientID(String gcmRegId){
        this.gcmRegId = gcmRegId;
    }

    public static GCMClientID createGCMClientID(String gcmRegId){
        if(gcmClientID == null){
            return gcmClientID = new GCMClientID(gcmRegId);
        }else{
            return gcmClientID;
        }
    }

    public String getGcmRegId() {
        return gcmRegId;
    }

    public void setGcmRegId(String gcmRegId) {
        this.gcmRegId = gcmRegId;
    }
}
