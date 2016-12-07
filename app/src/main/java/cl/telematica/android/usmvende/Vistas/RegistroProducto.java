package cl.telematica.android.usmvende.Vistas;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.lang.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import cl.telematica.android.usmvende.Adapters.MyAdapterComprador;
import cl.telematica.android.usmvende.EnvioData;
import cl.telematica.android.usmvende.Models.BaseDatosSqlite;
import cl.telematica.android.usmvende.R;
import cl.telematica.android.usmvende.Models.Producto;


public class RegistroProducto extends AppCompatActivity implements LocationListener {

    EditText txtNP, txtDP, txtPP, txtNV;
    TextView mLatitudeData;
    TextView mLongitudeData;
    /*______________________________________________*/
    private RecyclerView recyclerView;
    private MyAdapterComprador adapter;
    //private MyAdapterComprador adapter2;
    private List<Producto> listProduct;
    private FloatingActionButton fab;
    /*______________________________________________*/
    private LocationManager mLocationManager;
    private final String GPS = LocationManager.GPS_PROVIDER; //Nombre del proveedor de ubicación GPS.
    private final String NET = LocationManager.NETWORK_PROVIDER; //Nombre del proveedor de ubicación de red.

    private static final long minTime = 15000; //Mínimo intervalo de tiempo entre actualizaciones de ubicación, en milisegundos
    private static final float minDistance = 0; //Distancia mínima entre las actualizaciones de ubicación, en metros

    double lat, lng;
    Context mcontext;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_rg_pro);

        mcontext = this;
        activity = this;
        mLatitudeData = (TextView) findViewById(R.id.latitude);
        mLongitudeData = (TextView) findViewById(R.id.longitude);
        mLocationManager =(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //adapter2=  new MyAdapterComprador(activity,listProduct);
        /*____________________________________*/

        recyclerView = (RecyclerView) findViewById(R.id.recyle_view);
        listProduct = new ArrayList<Producto>();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        fab.setOnClickListener(onAddingListener());
        /*____________________________________*/

        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                String result = POST("http://usmvende.telprojects.xyz/vendedor", "\""+consulta(mcontext)+"\"");
                //String result = POST("http://usmvende.telprojects.xyz/vendedor", "\""+"gsgsgs"+"\"");
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                System.out.println(result);
                if (result != null) {
                    String Long;
                    String Lati;
                    // specify an adapter (see also next example)
                    listProduct = getListProduct(result);
                    adapter = new MyAdapterComprador(activity, listProduct);
                    adapter.setSwitch(0);
                    //adapter2 = adapter;
                    Toast.makeText(mcontext, "Obteniendo localizacion...", Toast.LENGTH_SHORT).show();
                    Lati=mLatitudeData.getText().toString();
                    Long=mLongitudeData.getText().toString();
                    adapter.actualizarUbicacion(Lati,Long);
                    recyclerView.setAdapter(adapter);
                }
            }
        };
        task.execute();

    }

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
        Log.d("USUARIO CONSULTA REG",user);
        return user;
    }
    //Metodo que realiza la conexion y procesa los datos de envio y recibo
    public static String POST(String targeturl, String msg) {
        String result = "";
        String json = "";
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

            OutputStream os = connection.getOutputStream();
            os.write(msg.getBytes());
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

    public List<Producto> getListProduct(String result) {
        List<Producto> listaProducto = new ArrayList<Producto>();

        try {
            JSONArray lista = new JSONArray(result);

            int size = lista.length();
            for (int i = 0; i < size; i++) {
                Producto producto = new Producto();
                JSONObject objeto = lista.getJSONObject(i);

                producto.setNombreP(objeto.getString("producto"));
                producto.setNombreV(null);
                producto.setPrecio(objeto.getString("precio"));
                producto.setDescripcion(objeto.getString("descripcion"));
                producto.setSell(objeto.getString("vendiendo"));
                producto.setFav(null);
                listaProducto.add(producto);
            }
            return listaProducto;
        }
        catch(JSONException e){
            e.printStackTrace();
            return listaProducto;
        }
    }



    @Override
    public void onResume(){
        super.onResume();
        //Condiciones para determinar si los de GPS o NET estan habilitados
        if (!mLocationManager.isProviderEnabled(GPS)) {//Devuelve el estado actual activado / desactivado del proveedor determinado.
            Toast.makeText(this, getString(R.string.location_error_msg), Toast.LENGTH_LONG).show();
        }
        else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.permission_error_msg), Toast.LENGTH_LONG).show();
                return;
            }
            if(mLocationManager.isProviderEnabled(GPS)){
                mLocationManager.requestLocationUpdates(GPS, minTime, minDistance, this); //Registrarse para actualizaciones de ubicación utilizando el proveedor de llamada, y un LocationListener especificado.
            }
        }
        Toast.makeText(this,"Localizacion Retomada", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getString(R.string.permission_error_msg), Toast.LENGTH_LONG).show();
            return;
        }
        mLocationManager.removeUpdates(this); //Elimina todas las actualizaciones de ubicación para el LocationListener especificado
        Toast.makeText(this,"Localizacion Pausada", Toast.LENGTH_SHORT).show();
    }


    private View.OnClickListener onAddingListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(RegistroProducto.this);
                dialog.setContentView(R.layout.dialog_add); //layout for dialog
                dialog.setTitle("Agregar Producto");
                dialog.setCancelable(false); //none-dismiss when touching outside Dialog

                // set the custom dialog components - texts and image
                txtNP = (EditText) dialog.findViewById(R.id.txtNombreProducto);
                txtDP = (EditText) dialog.findViewById(R.id.txtDescpProducto);
                txtPP = (EditText) dialog.findViewById(R.id.txtPrecioProducto);

                View btnAdd = dialog.findViewById(R.id.btn_ok);
                View btnCancel = dialog.findViewById(R.id.btn_cancel);

                btnAdd.setOnClickListener(onConfirmListener(txtNP, txtDP, txtPP, dialog));
                btnCancel.setOnClickListener(onCancelListener(dialog));

                dialog.show();
            }
        };
    }

    private View.OnClickListener onConfirmListener(final EditText txtNP,final EditText txtDP, final EditText txtPP,final Dialog dialog){
        return new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Producto producto = new Producto();
                producto.setNombreP(txtNP.getText().toString());
                producto.setDescripcion((txtDP.getText().toString()));
                producto.setPrecio(txtPP.getText().toString());
                EnvioData envioData = new EnvioData(producto.getNombreP(),
                                                    producto.getPrecio(),
                                                    producto.getDescripcion(),
                                                    activity);
                envioData.sendRegister();
                listProduct.add(producto);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        };
    }

    private View.OnClickListener onCancelListener(final Dialog dialog) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };
    }

    @Override
    public void onLocationChanged(Location mLocation) {
        if (mLocation != null){
            mLatitudeData.setText(Double.toString(mLocation.getLatitude()));
            mLongitudeData.setText(Double.toString(mLocation.getLongitude()));
        }
        else{
            Toast.makeText(this,"Localizacion NULL", Toast.LENGTH_SHORT).show();
        }

         //lat = location.getLatitude();
         //lng = location.getLongitude();
        //adapter2.actualizarUbicacion(lat,lng);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(this, getString(R.string.status_msg) + "--> Provider: " + provider + " Status: " + status, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
