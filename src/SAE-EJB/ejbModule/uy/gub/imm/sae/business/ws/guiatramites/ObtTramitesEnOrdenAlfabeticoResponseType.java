
package uy.gub.imm.sae.business.ws.guiatramites;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for obtTramitesEnOrdenAlfabeticoResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="obtTramitesEnOrdenAlfabeticoResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="totalResultados" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="colTramites" type="{http://tempuri.org/}ArrayOfResumenTramite" minOccurs="0"/>
 *         &lt;element name="advertencias" type="{http://tempuri.org/}ArrayOfMensaje" minOccurs="0"/>
 *         &lt;element name="errores" type="{http://tempuri.org/}ArrayOfMensaje" minOccurs="0"/>
 *         &lt;element name="letrasDeshabilitadas" type="{http://tempuri.org/}ArrayOfString" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtTramitesEnOrdenAlfabeticoResponseType", propOrder = {
    "totalResultados",
    "colTramites",
    "advertencias",
    "errores",
    "letrasDeshabilitadas"
})
public class ObtTramitesEnOrdenAlfabeticoResponseType {

    protected int totalResultados;
    protected ArrayOfResumenTramite colTramites;
    protected ArrayOfMensaje advertencias;
    protected ArrayOfMensaje errores;
    protected ArrayOfString letrasDeshabilitadas;

    /**
     * Gets the value of the totalResultados property.
     * 
     */
    public int getTotalResultados() {
        return totalResultados;
    }

    /**
     * Sets the value of the totalResultados property.
     * 
     */
    public void setTotalResultados(int value) {
        this.totalResultados = value;
    }

    /**
     * Gets the value of the colTramites property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfResumenTramite }
     *     
     */
    public ArrayOfResumenTramite getColTramites() {
        return colTramites;
    }

    /**
     * Sets the value of the colTramites property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfResumenTramite }
     *     
     */
    public void setColTramites(ArrayOfResumenTramite value) {
        this.colTramites = value;
    }

    /**
     * Gets the value of the advertencias property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfMensaje }
     *     
     */
    public ArrayOfMensaje getAdvertencias() {
        return advertencias;
    }

    /**
     * Sets the value of the advertencias property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfMensaje }
     *     
     */
    public void setAdvertencias(ArrayOfMensaje value) {
        this.advertencias = value;
    }

    /**
     * Gets the value of the errores property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfMensaje }
     *     
     */
    public ArrayOfMensaje getErrores() {
        return errores;
    }

    /**
     * Sets the value of the errores property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfMensaje }
     *     
     */
    public void setErrores(ArrayOfMensaje value) {
        this.errores = value;
    }

    /**
     * Gets the value of the letrasDeshabilitadas property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getLetrasDeshabilitadas() {
        return letrasDeshabilitadas;
    }

    /**
     * Sets the value of the letrasDeshabilitadas property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setLetrasDeshabilitadas(ArrayOfString value) {
        this.letrasDeshabilitadas = value;
    }

}
