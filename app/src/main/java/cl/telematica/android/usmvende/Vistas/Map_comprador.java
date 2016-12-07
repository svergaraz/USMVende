package cl.telematica.android.usmvende.Vistas;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cl.telematica.android.usmvende.DataParser;
import cl.telematica.android.usmvende.R;


public class Map_comprador extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleMap mMap;
        ArrayList<LatLng> MarkerPoints;
        GoogleApiClient mGoogleApiClient;
        private Marker marcador;
        LocationRequest mLocationRequest;
        String info;
        String GPS_seller;
        Context mContext;
        double mlatitude, mlongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_comprador);

        info = getIntent().getStringExtra("sellprod");
        mContext = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        checkLocationPermission();
        }
        // Initializing
        MarkerPoints = new ArrayList<>();
        //Instanciamos el objeto mMap a partir del MapFragment definido bajo el Id "map"
        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_comprador));
        //get notified when the map is ready to be used.
        mapFragment.getMapAsync(this);
    }

@Override
public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        buildGoogleApiClient();
        //Activamos la capa MyLocation, la cual nos permite obtener la ubicación actual del ususario
        mMap.setMyLocationEnabled(true);
        }
        } else {
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
        }

        //SOLICITA COORDENADAS DEL VENDEDOR
        //proceso de obtencion de coordenadas del vendedor recibidas por notificacion
        String[] aux = info.split(",");
        Log.d("vendedor,producto: ", aux[0] + "," + aux[1]);

        new HttpAsyncTask().execute("http://usmvende.telprojects.xyz/getgps",aux[0],aux[1]);



        }
      private void agregarMarcador(double lat, double lng) {
        LatLng posClient = new LatLng(lat, lng);
        MarkerPoints.add(posClient);
        CameraUpdate miubicacion = CameraUpdateFactory.newLatLngZoom(posClient, 18);
          System.out.print("marcador comprador: "+marcador);
        if (marcador != null) {
            marcador.remove();
        }
            marcador = mMap.addMarker(new MarkerOptions().position(posClient).title("Comprador:" + lat + "/" + lng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.animateCamera(miubicacion);

        }

    //Metodo que realiza la conexion y procesa los datos de envio y recibo
    public static String POST(String targeturl,String vendedor,String producto) {
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

            // build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("vendedor",vendedor);
            jsonObject.put("producto",producto);

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
    //CLASE que extiende de AsyncTask para que en segundo plano conecte con el servidor y localice posicion
    public class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0], urls[1],urls[2]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //result="ok";
            String[] latlng = result.split(",");
            Log.d("PERRALOGIN",result);
            if(latlng.length == 2){

                LatLng posSeller = new LatLng(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]));
                MarkerPoints.add(posSeller);
                MarkerOptions options = new MarkerOptions();
                options.position(posSeller).title("Vendedor: " + latlng[0] + "/" + latlng[1]);
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                mMap.addMarker(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(posSeller));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

                //SOLICITA COORDENADAS DEL COMPRADOR
                miUbicacion();
            }
            else{
                Toast.makeText(mContext, "Operación fallida", Toast.LENGTH_SHORT).show();
            }
        }
    }//Asynctask
private void actualizarUbicacion(Location location) {
        if (location != null) {
        mlatitude = location.getLatitude();
        mlongitude = location.getLongitude();
            Log.d("comprador: ",mlatitude +","+mlongitude);
        if (MarkerPoints.size() >1){
        MarkerPoints.remove(1);
        }

        agregarMarcador(mlatitude, mlongitude);
        // Checks, whether start and end locations are captured
        LatLng origin = MarkerPoints.get(0);
        LatLng dest = MarkerPoints.get(1);
            Log.d("coodernadas: ",origin +","+dest);

        // Getting URL to the Google Directions API
        String url = getUrl(origin, dest);
        Log.d("onMapClick", url.toString());
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(dest));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));


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
        Toast.makeText(mContext,"Proveedores desactivados, Habilite GPS ",Toast.LENGTH_LONG).show();
        }
        else{
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return;
        }
            System.out.println("ANTESSS-");
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            System.out.println("ADENTRO-");
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,15000,0,loclistener);
        }
            System.out.println("Despuesss-");
        }

        }
private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
        }

/**
 * A method to download json data from url
 */
private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
        URL url = new URL(strUrl);
        // Creating an http connection to communicate with url
        urlConnection = (HttpURLConnection) url.openConnection();
        // Connecting to url
        urlConnection.connect();
        // Reading data from url
        iStream = urlConnection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
        StringBuffer sb = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
        sb.append(line);
        }
        data = sb.toString();
        Log.d("downloadUrl", data.toString());
        br.close();

        } catch (Exception e) {
        Log.d("Exception", e.toString());
        } finally {
        iStream.close();
        urlConnection.disconnect();
        }
        return data;
        }

// Fetches data from url passed
private class FetchUrl extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... url) {

        // For storing data from web service
        String data = "";

        try {
            // Fetching the data from web service
            data = downloadUrl(url[0]);
            Log.d("Background Task data", data.toString());
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        ParserTask parserTask = new ParserTask();

        // Invokes the thread for parsing the JSON data
        parserTask.execute(result);

    }
}

/**
 * A class to parse the Google Places in JSON format
 */
private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            Log.d("ParserTask",jsonData[0].toString());
            DataParser parser = new DataParser();
            Log.d("ParserTask", parser.toString());

            // Starts parsing data
            routes = parser.parse(jObject);
            Log.d("ParserTask","Executing routes");
            Log.d("ParserTask",routes.toString());

        } catch (Exception e) {
            Log.d("ParserTask",e.toString());
            e.printStackTrace();
        }
        return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;

        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(10);
            lineOptions.color(Color.GREEN);
            Log.d("onPostExecute","onPostExecute lineoptions decoded");

        }
        // Drawing polyline in the Google Map for the i-th route
        if(lineOptions != null) {
            mMap.addPolyline(lineOptions);
        }
        else {
            Log.d("onPostExecute","without Polylines drawn");
        }
    }
}


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        /*
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        */
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }
}
