package cl.telematica.android.usmvende.Vistas;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
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

import cl.telematica.android.usmvende.Models.BaseDatosSqlite;
import cl.telematica.android.usmvende.R;

//server key(usmvende)= AIzaSyCWVNhWkYq3Yt82xZ7QecGyyPAyCsuWipg
//sender ID(usmvende) = 480701540016
// Web API Key (usmvende) = AIzaSyDXuE44kGlHJiSDYfHY9SAdobTJ3PBPmsQ

public class menu extends AppCompatActivity {
    Button btnVender,btnComprar,cerrar_sesion;
    Context mcontext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mcontext = this;
        btnComprar = (Button) findViewById(R.id.btnComprador);
        btnVender = (Button) findViewById(R.id.btnVendedor);
        cerrar_sesion = (Button) findViewById(R.id.cerrar);

        btnVender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu.this, RegistroProducto.class);
                startActivity(intent);
            }
        });

        btnComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu.this, Comprador.class);
                startActivity(intent);
            }
        });

        cerrar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu.this, Login.class);
                new HttpAsyncTask().execute("http://usmvende.telprojects.xyz/logout", "\"" + consulta(mcontext) + "\"");
                BaseDatosSqlite dbInstance = new BaseDatosSqlite(mcontext);
                SQLiteDatabase db = dbInstance.getWritableDatabase();
                if (db != null) {
                    db.beginTransaction(); //inicializo al inicio del for donde hago las inserciones sin abrir ni cerrar la base de datos
                    try {
                        db.execSQL("delete from Logins where 1");
                    }
                    //luego al final fuera del for cuando termine
                    finally {
                        db.setTransactionSuccessful();
                    }
                    db.endTransaction();
                    db.close();
                }


                startActivity(intent);

            }
        });
        Toast.makeText(mcontext, consulta(mcontext), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(mcontext, "NO hay sesion iniciada (CERRAR)", Toast.LENGTH_SHORT).show();
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
    public class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String result=POST(urls[0],urls[1]);
            return result;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("RESPUESTA SERVIDOR DATO",result);
            if(result.equals("True")){
                Toast.makeText(mcontext, "Sesion cerrada!", Toast.LENGTH_LONG).show();
            }
            else if(result.equals("False"))
            {
                Toast.makeText(mcontext, "Sesion cerrada no ha sido cerrada!", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(mcontext, "Recuerda ver esto", Toast.LENGTH_LONG).show();
            }

        }
    }//Asynctask
}


