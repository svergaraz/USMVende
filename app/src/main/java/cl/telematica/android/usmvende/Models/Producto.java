package cl.telematica.android.usmvende.Models;


public class Producto {
    private String nombreP;
    private String descripcion;
    private String precio;
    private String nombreV;
    private String localizacion;
    private String topic;

    public void setNombreP(String nombre){
        this.nombreP = nombre;
    }

    public String getNombreP(){
        return nombreP ;
    }
    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }

    public String getDescripcion(){
        return descripcion;
    }

    public void setPrecio(String precio){
        this.precio = precio;
    }

    public String getPrecio(){
        return precio;
    }
    public void setNombreV(String nombreV){
        this.nombreV = nombreV;
    }

    public String getNombreV(){
        return nombreV;
    }

    public void setLocalizacion(String localizacion){
        this.localizacion= localizacion;
    }

    public String getLocalizacion(){
        return localizacion;
    }

    public void setTopic(String topic){

        this.topic= topic;
    }

    public String getTopic(){
        return topic;
    }
}
