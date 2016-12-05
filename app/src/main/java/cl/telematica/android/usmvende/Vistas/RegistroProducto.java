package cl.telematica.android.usmvende.Vistas;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
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
import java.util.StringTokenizer;

import cl.telematica.android.usmvende.Adapters.MyAdapterComprador;
import cl.telematica.android.usmvende.Presenters.LocationPresenterImpl;
import cl.telematica.android.usmvende.Interfaces.LocationView;
import cl.telematica.android.usmvende.R;
import cl.telematica.android.usmvende.Models.Producto;

public class RegistroProducto extends AppCompatActivity implements View.OnClickListener{ //LocationView

    EditText txtNP, txtDP, txtPP, txtNV;
    //Button btnRP, btnVP;
    Button btnRP;
    Switch mySwitch;
    //TextView tvIsConnected;
    //TextView mLatitudeData;
    //TextView mLongitudeData;
    TextView switchStatus;
    double mlatitude=0.0, mlongitude=0.0;


    /*______________________________________________*/
    private RecyclerView recyclerView;
    private MyAdapterComprador adapter;
    private ArrayList<Producto> listProduct;
    private FloatingActionButton fab;
    /*______________________________________________*/

    Producto person;
    String NP, DP, PP, NV;
    //Location location; // para guardar una lectura de coordenadas obtenida por el proveedor
    //LocationPresenterImpl mLocationPresenter; //para hacer uso de algunos metodos
    LocationManager locationManager; //para pasarle un servicio de localizacion
    //Boolean gpsactivo = false;
    Context mcontext;

    //Intent ix = getIntent();
    //String receive = (String) ix.getStringExtra("topic");

    //CLASE que extiende de AsyncTask para que en segundo plano conecte con el servidor y localice posicion
    public class HttpAsyncTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
        person = new Producto();
        person.setNombreP(urls[1]);
        person.setDescripcion(urls[2]);
        person.setPrecio(urls[3]);
        person.setNombreV(urls[4]);
        if(!urls[5].equals("") && !urls[6].equals("")){
            person.setLocalizacion(urls[5]+","+urls[6]);
        }
        return POST(urls[0], person);
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        Log.d("RESPUESTA SERVIDOR DATO",result);
        if(result.equals("True")){
            Toast.makeText(mcontext, "Datos Enviados!", Toast.LENGTH_LONG).show();
        }
        else if(result.equals("False"))
        {
            Toast.makeText(mcontext, "Error en envío de datos!", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(mcontext, "Recuerda ver esto", Toast.LENGTH_LONG).show();
        }

    }
}//Asynctask

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_registro_producto);

        /*____________________________________*/
        setContentView(R.layout.act_rg_pro);
        recyclerView = (RecyclerView) findViewById(R.id.recyle_view);
        listProduct = new ArrayList<Producto>();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //setRecyclerViewData();
        adapter = new MyAdapterComprador (this, listProduct);
        recyclerView.setLayoutManager(layoutManager);

        fab.setOnClickListener(onAddingListener());
        /*____________________________________*/

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE); //le doy el control sobre los servicios de localizacion que tenga el dispositivo
        //mLocationPresenter = new LocationPresenterImpl(this,locationManager,this); //
        mcontext = this;

        //mLatitudeData = (TextView) findViewById(R.id.latitudeData);
        //mLongitudeData = (TextView) findViewById(R.id.longitudeData);

        //Obtenemos una referencia a los controles de la interfaz
        /*
        txtNP = (EditText) findViewById(R.id.txtNombreProducto);
        txtDP = (EditText) findViewById(R.id.txtDescpProducto);
        txtPP = (EditText) findViewById(R.id.txtPrecioProducto);
        txtNV = (EditText) findViewById(R.id.txtNombreVendedor);
        */
        //tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);

        btnRP = (Button) findViewById(R.id.btnRegistrarProducto);
        //btnVP = (Button) findViewById(R.id.btnVenderProducto);
        //switchStatus= (TextView) findViewById(R.id.status);
        mySwitch = (Switch) findViewById(R.id.btnVenderProducto);
        /*
        // check if you are connected or not
        /*if (isConnected()) {
            tvIsConnected.setBackgroundColor(0xFF00CC00);
            tvIsConnected.setText("You are connected");
        } else {
            tvIsConnected.setText("You are NOT connected");
        }
        */

        }

        // add click listener to Button "POST"
       // btnRP.setOnClickListener(this);
        //btnVP.setOnClickListener(this);

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String NP, DP, PP, NV;
                NP = txtNP.getText().toString();
                DP = txtDP.getText().toString();
                PP = txtPP.getText().toString();
                NV = txtNV.getText().toString();

                if(isChecked){
                    switchStatus.setText("Vendiendo");
                    switchStatus.setBackgroundColor(0xFF00CC00);
                    //String Long;
                    //String Lati;
                    if (ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    else
                    {
                        /*
                        //Toast.makeText(this, "Obteniendo localizacion...", Toast.LENGTH_LONG).show();
                        Lati=mLatitudeData.getText().toString();
                        Long=mLongitudeData.getText().toString();
                        */
                        miUbicacion();

                    }
                    //Toast.makeText(this, "Ejecutando hilo..", Toast.LENGTH_LONG).show();
                    new HttpAsyncTask().execute("http://usmvende.telprojects.xyz/vender", NP, DP, PP, NV, Double.toString(mlatitude),Double.toString(mlongitude));
                }else{
                    switchStatus.setText("No Vendiendo");
                    switchStatus.setBackgroundColor(0xFFFF0000);
                }
            }

        });
    }


    // Metodo encargado de la implementacion de los botones y la obtencion de datos
    @Override
    public void onClick(View v) {
        String NP, DP, PP, NV;
        NP = txtNP.getText().toString();
        DP = txtDP.getText().toString();
        PP = txtPP.getText().toString();
        NV = txtNV.getText().toString();

        switch (v.getId()) {
            case R.id.btnRegistrarProducto:
                if (!validate()) {
                    Toast.makeText(this, "Ingrese algun dato!", Toast.LENGTH_LONG).show();
                    break;
                }
                new HttpAsyncTask().execute("http://usmvende.telprojects.xyz/nuevo_producto", NP, DP, PP, NV,"","");
                break;
        }

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
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ){
            //mensaje error e indicar que se encienda gps o internet
            Toast.makeText(this,"Proveedores desactivados, Habilite GPS ",Toast.LENGTH_LONG).show();
        }
        else{
            if (ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.permission_error_msg), Toast.LENGTH_LONG).show();
                return;
            }
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                actualizarUbicacion(location);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,15000,0,loclistener);
            }

        }

    }
    //Metodo que realiza la conexion y procesa los datos de envio y recibo
    public static String POST(String targeturl, Producto person) {
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
                jsonObject.put("producto", person.getNombreP());
                jsonObject.put("descripcion", person.getDescripcion());
                jsonObject.put("precio", person.getPrecio());
                jsonObject.put("vendedor", person.getNombreV());
            }
            else if (third.trim().equals("vender")) {
                jsonObject.put("vendedor", person.getNombreV());
                System.out.println(person.getLocalizacion());
                jsonObject.put("gps", person.getLocalizacion());
            }
            else {
            }

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


    //Metodo para asegurarse de que no se envie info en vacia
    private boolean validate() {
        if (txtNP.getText().toString().trim().equals(""))
            return false;
        else if (txtDP.getText().toString().trim().equals(""))
            return false;
        else if (txtPP.getText().toString().trim().equals(""))
            return false;
        else if (txtNV.getText().toString().trim().equals(""))
            return false;
        else
            return true;
    }//Validate
    /*
    // Metodo para saber si huboo conexion exitosa o no cn el servidor
    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }//isConnected

    //Metodo para convertir stream respuesta del servidor en string
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }//convertInpuntStreamToString
    */

    //Metodo de ciclo vida del activity principal Registrar Producto
    @Override
    public void onResume(){
        super.onResume();
        if (ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getString(R.string.permission_error_msg), Toast.LENGTH_LONG).show();
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,15000,0,loclistener);
        //mLocationPresenter.startUpdates(); //metodo que permite iniciar busquead de localizaciones medidas por el proveedor
        Toast.makeText(this,"Localizacion Retomada", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        //mLocationPresenter.stopUpdates();//metodo que detiene la busqueda de localizaciones medidas por el proveedor
        if (ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getString(R.string.permission_error_msg), Toast.LENGTH_LONG).show();
            return;
        }
        locationManager.removeUpdates(loclistener);
        Toast.makeText(this,"Localizacion Pausada", Toast.LENGTH_SHORT).show();
    }


    /*
    //Metodos de LocationView
    @Override
    public void showLocationErrorMsg() {
        Toast.makeText(this, getString(R.string.location_error_msg), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showPermissionErrorMsg() {
        Toast.makeText(this, getString(R.string.permission_error_msg), Toast.LENGTH_LONG).show();
    }

    @Override
    public void manageLocationChange(Location mLocation) {
        mLatitudeData.setText("" + mLocation.getLatitude());
        mLongitudeData.setText("" + mLocation.getLongitude());
        //person.setLocalizacion(String.valueOf(mLocation.getLatitude())+"/"+ String.valueOf(mLocation.getLongitude()));
    }

    @Override
    public void manageStatusChange(String provider, int status) {
        Toast.makeText(this, getString(R.string.status_msg) + "--> Provider: " + provider + " Status: " + status, Toast.LENGTH_LONG).show();
    }
    */

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
                txtPP = (EditText) dialog.findViewById(R.id.txtDescpProducto);

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

                listProduct.add(producto);
                adapter.notify();
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
}
