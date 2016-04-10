package co.edu.udistrital.dulcesparamiamor.wsresponse;

/**
 * Created by JulioS on 10/04/2016.
 */
public class OSAutenticar {
    protected int codigoRespuesta;
    protected String mensajeRespuesta;

    /**
     * Obtiene el valor de la propiedad codigoRespuesta.
     *
     */
    public int getCodigoRespuesta() {
        return codigoRespuesta;
    }

    /**
     * Define el valor de la propiedad codigoRespuesta.
     *
     */
    public void setCodigoRespuesta(int value) {
        this.codigoRespuesta = value;
    }

    /**
     * Obtiene el valor de la propiedad mensajeRespuesta.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMensajeRespuesta() {
        return mensajeRespuesta;
    }

    /**
     * Define el valor de la propiedad mensajeRespuesta.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMensajeRespuesta(String value) {
        this.mensajeRespuesta = value;
    }
}
