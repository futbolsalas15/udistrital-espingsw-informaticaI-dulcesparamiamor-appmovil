package co.edu.udistrital.dulcesparamiamor.model.webservices;

/**
 * Created by JulioS on 03/04/2016.
 */
public class Response {
    private int codigo;
    private String mensaje ;
    private String token;

    public int getCodigo() {
        return codigo;
    }
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
    public String getMensaje() {
        return mensaje;
    }
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}