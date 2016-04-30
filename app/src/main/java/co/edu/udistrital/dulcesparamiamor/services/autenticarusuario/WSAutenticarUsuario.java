package co.edu.udistrital.dulcesparamiamor.services.autenticarusuario;

//------------------------------------------------------------------------------
// <wsdl2code-generated>
//    This code was generated by http://www.wsdl2code.com version  2.6
//
// Date Of Creation: 4/10/2016 7:42:35 PM
//    Please dont change this code, regeneration will override your changes
//</wsdl2code-generated>
//
//------------------------------------------------------------------------------
//
//This source code was auto-generated by Wsdl2Code  Version
//

import java.util.List;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.HeaderProperty;
import java.util.Hashtable;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import android.os.AsyncTask;
import org.ksoap2.serialization.MarshalFloat;

public class WSAutenticarUsuario {
    
    public String NAMESPACE ="http://tempuri.org/";
    public String url="http://proyectoweb-5g07603u.cloudapp.net/ServiceWeb/autenticarUsuario/ws_servidor_autenticarUsuario.asmx?wsdl";
    public int timeOut = 600;
    public IWsdl2CodeEvents eventHandler;
    public WS_Enums.SoapProtocolVersion soapVersion;
    
    public WSAutenticarUsuario(){}
    
    public WSAutenticarUsuario(IWsdl2CodeEvents eventHandler)
    {
        this.eventHandler = eventHandler;
    }
    public WSAutenticarUsuario(IWsdl2CodeEvents eventHandler, String url)
    {
        this.eventHandler = eventHandler;
        this.url = url;
    }
    public WSAutenticarUsuario(IWsdl2CodeEvents eventHandler, String url, int timeOutInSeconds)
    {
        this.eventHandler = eventHandler;
        this.url = url;
        this.setTimeOut(timeOutInSeconds);
    }
    public void setTimeOut(int seconds){
        this.timeOut = seconds * 1000;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public void autenticarUsuarioAsync(OEAutenticar u) throws Exception{
        if (this.eventHandler == null)
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        autenticarUsuarioAsync(u, null);
    }
    
    public void autenticarUsuarioAsync(final OEAutenticar u,final List<HeaderProperty> headers) throws Exception{
        
        new AsyncTask<Void, Void, OSAutenticar>(){
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            };
            @Override
            protected OSAutenticar doInBackground(Void... params) {
                return autenticarUsuario(u, headers);
            }
            @Override
            protected void onPostExecute(OSAutenticar result)
            {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null){
                    eventHandler.Wsdl2CodeFinished("autenticarUsuario", result);
                }
            }
        }.execute();
    }
    
    public OSAutenticar autenticarUsuario(OEAutenticar u){
        return autenticarUsuario(u, null);
    }
    
    public OSAutenticar autenticarUsuario(OEAutenticar u,List<HeaderProperty> headers){
        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.implicitTypes = true;
        soapEnvelope.dotNet = true;
        SoapObject soapReq = new SoapObject("http://tempuri.org/","autenticarUsuario");
        soapEnvelope.addMapping("http://tempuri.org/","u",new OEAutenticar().getClass());
        soapReq.addProperty("u",u);
        soapEnvelope.setOutputSoapObject(soapReq);
        HttpTransportSE httpTransport = new HttpTransportSE(url,timeOut);
        try{
            if (headers!=null){
                httpTransport.call("http://tempuri.org/autenticarUsuario", soapEnvelope,headers);
            }else{
                httpTransport.call("http://tempuri.org/autenticarUsuario", soapEnvelope);
            }
            Object retObj = soapEnvelope.bodyIn;
            if (retObj instanceof SoapFault){
                SoapFault fault = (SoapFault)retObj;
                Exception ex = new Exception(fault.faultstring);
                if (eventHandler != null)
                    eventHandler.Wsdl2CodeFinishedWithException(ex);
            }else{
                SoapObject result=(SoapObject)retObj;
                if (result.getPropertyCount() > 0){
                    Object obj = result.getProperty(0);
                    SoapObject j = (SoapObject)obj;
                    OSAutenticar resultVariable =  new OSAutenticar (j);
                    return resultVariable;
                    
                }
            }
        }catch (Exception e) {
            if (eventHandler != null)
                eventHandler.Wsdl2CodeFinishedWithException(e);
            e.printStackTrace();
        }
        return null;
    }
    
}