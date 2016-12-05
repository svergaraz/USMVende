package cl.telematica.android.usmvende;

import android.app.Activity;

import cl.telematica.android.usmvende.Vistas.RegistroProducto;

/**
 * Created by simon on 04-12-2016.
 */

public class EnvioData {

    private String Nprod;
    private String Dprod;
    private String Pprod;
    private Activity activity;

    public EnvioData(String Nprod, String Dprod, String Pprod, Activity activity){
        this.Nprod = Nprod;
        this.Dprod = Dprod;
        this.Pprod = Pprod;
        this.activity = activity;
    }

    public void send(){

    }
}
