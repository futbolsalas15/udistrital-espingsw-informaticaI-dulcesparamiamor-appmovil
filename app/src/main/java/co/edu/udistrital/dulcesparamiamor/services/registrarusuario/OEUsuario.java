package co.edu.udistrital.dulcesparamiamor.services.registrarusuario;


//------------------------------------------------------------------------------
// <wsdl2code-generated>
//    This code was generated by http://www.wsdl2code.com version  2.6
//
// Date Of Creation: 4/10/2016 7:46:02 PM
//    Please dont change this code, regeneration will override your changes
//</wsdl2code-generated>
//
//------------------------------------------------------------------------------
//
//This source code was auto-generated by Wsdl2Code  Version
//
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import java.util.Hashtable;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

public class OEUsuario implements KvmSerializable {
    
    public String nombre;
    public String correo;
    public String clave;
    public int telefono;
    public String nombreAmor;
    public String correoAmor;
    public int telefonoAmor;
    public String facebookAmor;
    public String imagenAmor;
    
    public OEUsuario(){}
    
    public OEUsuario(SoapObject soapObject)
    {
        if (soapObject == null)
            return;
        if (soapObject.hasProperty("nombre"))
        {
            Object obj = soapObject.getProperty("nombre");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                nombre = j.toString();
            }else if (obj!= null && obj instanceof String){
                nombre = (String) obj;
            }
        }
        if (soapObject.hasProperty("correo"))
        {
            Object obj = soapObject.getProperty("correo");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                correo = j.toString();
            }else if (obj!= null && obj instanceof String){
                correo = (String) obj;
            }
        }
        if (soapObject.hasProperty("clave"))
        {
            Object obj = soapObject.getProperty("clave");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                clave = j.toString();
            }else if (obj!= null && obj instanceof String){
                clave = (String) obj;
            }
        }
        if (soapObject.hasProperty("telefono"))
        {
            Object obj = soapObject.getProperty("telefono");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                telefono = Integer.parseInt(j.toString());
            }else if (obj!= null && obj instanceof Number){
                telefono = (Integer) obj;
            }
        }
        if (soapObject.hasProperty("nombreAmor"))
        {
            Object obj = soapObject.getProperty("nombreAmor");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                nombreAmor = j.toString();
            }else if (obj!= null && obj instanceof String){
                nombreAmor = (String) obj;
            }
        }
        if (soapObject.hasProperty("correoAmor"))
        {
            Object obj = soapObject.getProperty("correoAmor");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                correoAmor = j.toString();
            }else if (obj!= null && obj instanceof String){
                correoAmor = (String) obj;
            }
        }
        if (soapObject.hasProperty("telefonoAmor"))
        {
            Object obj = soapObject.getProperty("telefonoAmor");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                telefonoAmor = Integer.parseInt(j.toString());
            }else if (obj!= null && obj instanceof Number){
                telefonoAmor = (Integer) obj;
            }
        }
        if (soapObject.hasProperty("facebookAmor"))
        {
            Object obj = soapObject.getProperty("facebookAmor");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                facebookAmor = j.toString();
            }else if (obj!= null && obj instanceof String){
                facebookAmor = (String) obj;
            }
        }
        if (soapObject.hasProperty("imagenAmor"))
        {
            Object obj = soapObject.getProperty("imagenAmor");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                imagenAmor = j.toString();
            }else if (obj!= null && obj instanceof String){
                imagenAmor = (String) obj;
            }
        }
    }
    @Override
    public Object getProperty(int arg0) {
        switch(arg0){
            case 0:
                return nombre;
            case 1:
                return correo;
            case 2:
                return clave;
            case 3:
                return telefono;
            case 4:
                return nombreAmor;
            case 5:
                return correoAmor;
            case 6:
                return telefonoAmor;
            case 7:
                return facebookAmor;
            case 8:
                return imagenAmor;
        }
        return null;
    }
    
    @Override
    public int getPropertyCount() {
        return 9;
    }
    
    @Override
    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "nombre";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "correo";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "clave";
                break;
            case 3:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "telefono";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "nombreAmor";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "correoAmor";
                break;
            case 6:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "telefonoAmor";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "facebookAmor";
                break;
            case 8:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "imagenAmor";
                break;
        }
    }

    
    @Override
    public void setProperty(int arg0, Object arg1) {
    }
    
}
