package cl.telematica.android.usmvende;


import android.app.Activity;


import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

import cl.telematica.android.usmvende.Models.BaseDatosSqlite;
import cl.telematica.android.usmvende.Models.Producto;
import cl.telematica.android.usmvende.Vistas.RegistroProducto;


public class EnvioData {
    private String Nprod;
    private String Nseller;
    private String Nprecio;
    private String Ndescp;

    private Activity activity;
    public double mlatitude = 0.0;
    public double mlongitude = 0.0;

    public EnvioData(String Nprod, Activity activity) {
        this.Nprod = Nprod;
        this.activity = activity;
        //this.Nseller = consulta(this.activity);
        this.Nseller = "gsgsgs";
    }

    public void send() {

        LocationManager locationManager = (LocationManager) activity.getSystemService(activity.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
                        /*
                        //Toast.makeText(this, "Obteniendo localizacion...", Toast.LENGTH_LONG).show();
                        Lati=mLatitudeData.getText().toString();
                        Long=mLongitudeData.getText().toString();
                        */
            miUbicacion();

        }
        //Toast.makeText(this, "Ejecutando hilo..", Toast.LENGTH_LONG).show();
        new HttpAsyncTask().execute("http://usmvende.telprojects.xyz/vender",this.Nseller,"","","", Double.toString(mlatitude), Double.toString(mlongitude));
    }

    public void sendRegister(){
        new HttpAsyncTask().execute("http://usmvende.telprojects.xyz/nuevo_producto",this.Nseller,this.Nprod,this.Nprecio,this.Ndescp,"","");
    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            mlatitude = location.getLatitude();
            mlongitude = location.getLongitude();
        }

    }

    LocationListener loclistener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void miUbicacion() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //mensaje error e indicar que se encienda gps o internet
            Toast.makeText(activity, "Proveedores desactivados, Habilite GPS ", Toast.LENGTH_LONG).show();
        } else {
            if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, activity.getString(R.string.permission_error_msg), Toast.LENGTH_LONG).show();
                return;
            }
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                actualizarUbicacion(location);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, loclistener);
            }

        }

    }

    //Metodo que realiza la conexion y procesa los datos de envio y recibo
    public String POST(String targeturl,String Nvendedor,String Nprod, String Nprecio,String Ndescp, String gps) {
        String result = "";
        String json = "";
        StringTokenizer tokens = new StringTokenizer(targeturl, "/");
        String first = tokens.nextToken();
        String second = tokens.nextToken();
        String third = tokens.nextToken();
        StringBuffer response = new StringBuffer();
        try {
            URL url = new URL(targeturl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(5000);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // build jsonObject
            JSONObject jsonObject = new JSONObject();
            if (third.trim().equals("nuevo_producto")) {
                jsonObject.put("producto", Nprod);
                jsonObject.put("descripcion", Ndescp);
                jsonObject.put("precio", Nprecio);
                jsonObject.put("vendedor", Nvendedor);
            }
            else if (third.trim().equals("vender")) {
                jsonObject.put("vendedor", Nvendedor);
                System.out.println(gps);
                //jsonObject.put("producto", Nprod);
                jsonObject.put("gps",gps);
            }
            else {
            }
            /*
            // build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("vendedor", Nvendedor);
            //jsonObject.put("producto", Nproducto);
            jsonObject.put("gps", gps);
            */
            //convert JSONObject to JSON to String
            json = jsonObject.toString();
            System.out.println(json);
            OutputStream os = connection.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;


            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            connection.disconnect();


        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }


        return response.toString();
    }//POST

    public class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String gps;
            gps = urls[5]+","+urls[6];
            return POST(urls[0],urls[1],urls[2],urls[3],urls[4],gps);
            //gps = urls[2]+","+urls[3];
            //return POST(urls[0],urls[1],gps);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("RESPUESTA SERVIDOR DATO",result);
            if(result.equals("True")){
                Toast.makeText(activity, "Datos Enviados!", Toast.LENGTH_LONG).show();
            }
            else if(result.equals("False"))
            {
                Toast.makeText(activity, "Error en envío de datos!", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(activity, "Recuerda ver esto", Toast.LENGTH_LONG).show();
            }

        }
    }//Asynctask
    public String consulta(Context mcontext){
        BaseDatosSqlite dbInstance = new BaseDatosSqlite(mcontext);
        SQLiteDatabase db = dbInstance.getWritableDatabase();
        String user="";
        if(db != null){
            db.beginTransaction(); //inicializo al inicio del for donde hago las inserciones sin abrir ni cerrar la base de datos
            try{
                Cursor c = db.rawQuery("SELECT Usuario_actual from Logins", null);
                if (c.moveToFirst()) {  //Nos aseguramos de que existe al menos un registro
                    int i= 1;
                    do {  //Recorremos el cursor hasta que no haya más registros
                        user = c.getString(0);
                        i= i + 1;
                    } while(c.moveToNext());

                }
                else {
                    Toast.makeText(mcontext, "No existe usuario logueado", Toast.LENGTH_SHORT).show();
                }

            }
            //luego al final fuera del for cuando termine
            finally {
                db.setTransactionSuccessful();
            }
            db.endTransaction();
            db.close();
        }
        return user;
    }
}
