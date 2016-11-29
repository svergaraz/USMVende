package cl.telematica.android.usmvende.Vistas;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cl.telematica.android.usmvende.R;

//server key(usmvende)= AIzaSyCWVNhWkYq3Yt82xZ7QecGyyPAyCsuWipg
//sender ID(usmvende) = 480701540016
// Web API Key (usmvende) = AIzaSyDXuE44kGlHJiSDYfHY9SAdobTJ3PBPmsQ

public class menu extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //private BroadcastReceiver mRegistrationBroadcastReceiver;
    Button btnVender;
    Button btnComprar;
    public String token;
    Context mcontext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mcontext = this;
        btnComprar = (Button) findViewById(R.id.btnComprador);
        btnVender = (Button) findViewById(R.id.btnVendedor);

        btnVender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu.this,RegistroProducto.class);
                startActivity(intent);
            }
        });

        btnComprar.setOnClickListener(3.0new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu.this,Comprador.class);
                startActivity(intent);
            }
        });

        token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Token: " + token);
        Toast.makeText(menu.this, token, Toast.LENGTH_SHORT).show();

        // [END handle_data_extras]
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                   String result=POST("http://usmvende.telprojects.xyz/tokens",token);
                   return result;
            }

            @Override
            protected void onPostExecute(String result) {
                Toast.makeText(mcontext, "Token Enviado!", Toast.LENGTH_LONG).show();
            }
        };

        task.execute();


    }//task

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }//convertInpuntStreamToString

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

            // build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("token", msg);

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

}


