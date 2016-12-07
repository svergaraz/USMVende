package cl.telematica.android.usmvende.Models;


import android.os.Parcel;
import android.os.Parcelable;

public class Producto/* implements Parcelable*/ {
    private String nombreP;
    private String descripcion;
    private String precio;
    private String nombreV;
    private String fav;
    private String sell;

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

    public void setFav(String fav){
        this.fav = fav;
    }

    public String getFav(){return fav;}

    public void setSell(String sell){this.sell = sell;}
    public String getSell(){return sell;}

   /* public Producto(Parcel parcel){
        nombreP=parcel.readString();
        descripcion=parcel.readString();
        precio=parcel.readString();
        nombreV=parcel.readString();
        localizacion=parcel.readString();
        topic=parcel.readString();
        fav=parcel.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombreP);
        dest.writeString(descripcion);
        dest.writeString(precio);
        dest.writeString(nombreV);
        dest.writeString(localizacion);
        dest.writeString(topic);
        dest.writeString(fav);
    }

    public static final Parcelable.Creator<Producto> CREATOR = new
            Parcelable.Creator<Producto>() {
                public Producto createFromParcel(Parcel in) {
                    return new Producto(in);
                }

                public Producto[] newArray(int size) {
                    return new Producto[size];
                }};*/

}
