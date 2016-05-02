package co.edu.udistrital.dulcesparamiamor.services.autenticarusuario;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

public class OEAutenticar implements KvmSerializable {

    public String correo;
    public String clave;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String token;

    public OEAutenticar() {
    }

    public OEAutenticar(SoapObject soapObject) {
        if (soapObject == null)
            return;
        if (soapObject.hasProperty("correo")) {
            Object obj = soapObject.getProperty("correo");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                correo = j.toString();
            } else if (obj != null && obj instanceof String) {
                correo = (String) obj;
            }
        }
        if (soapObject.hasProperty("clave")) {
            Object obj = soapObject.getProperty("clave");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                clave = j.toString();
            } else if (obj != null && obj instanceof String) {
                clave = (String) obj;
            }
        }
        if (soapObject.hasProperty("token")) {
            Object obj = soapObject.getProperty("token");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                SoapPrimitive j = (SoapPrimitive) obj;
                token = j.toString();
            } else if (obj != null && obj instanceof String) {
                token = (String) obj;
            }
        }
    }

    @Override
    public Object getProperty(int arg0) {
        switch (arg0) {
            case 0:
                return correo;
            case 1:
                return clave;
            case 2:
                return token;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 3;
    }

    @Override
    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
        switch (index) {
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "correo";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "clave";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "token";
                break;
        }
    }

    @Override
    public void setProperty(int arg0, Object arg1) {
    }

}
