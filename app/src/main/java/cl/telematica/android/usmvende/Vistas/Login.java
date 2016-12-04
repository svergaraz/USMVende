package cl.telematica.android.usmvende.Vistas;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cl.telematica.android.usmvende.Models.BaseDatosSqlite;
import cl.telematica.android.usmvende.R;

public class Login extends AppCompatActivity {

    EditText password,usermail;
    Button login, register;
    Context mcontext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        password = (EditText)findViewById(R.id.pass);
        usermail = (EditText)findViewById(R.id.email);
        login = (Button)findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);
        mcontext=this;
        String logueado = setvalidacion(this);
        if (logueado != null){
            Toast.makeText(this, "Usuario: "+logueado+"se encuentra logueado", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,menu.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "No existe usuario logueado, Ingrese sus datos", Toast.LENGTH_LONG).show();
        }
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Paso_registro();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpAsyncTask().execute("URL",usermail.getText().toString(),password.getText().toString());
            }
        });
    }
    //Metodo que realiza la conexion y procesa los datos de envio y recibo
    public static String POST(String targeturl,String user,String pass) {
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
            jsonObject.put("email",user);
            jsonObject.put("pass",pass);

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

    public String setvalidacion(Context mcontext) {
        BaseDatosSqlite dbInstance = new BaseDatosSqlite(mcontext);
        SQLiteDatabase db = dbInstance.getReadableDatabase();
        String email_db = null;
        if (db != null) {
            Cursor c = db.rawQuery("SELECT Usuario_actual from Logins", null);
            if (c.moveToFirst()) {  //Nos aseguramos de que existe al menos un registro
                int i = 1;
                do {  //Recorremos el cursor hasta que no haya más registros
                    email_db = c.getString(0);
                    i = i + 1;
                } while (c.moveToNext());

            }
        }
        db.close();
        return email_db;
    }

    public void insertlogin(Context mcontext,String email){
        BaseDatosSqlite dbInstance = new BaseDatosSqlite(mcontext);
        SQLiteDatabase db = dbInstance.getWritableDatabase();
        if(db != null){
            db.beginTransaction(); //inicializo al inicio del for donde hago las inserciones sin abrir ni cerrar la base de datos
            try{
                Cursor c = db.rawQuery("SELECT Usuario_actual from Logins", null);
                if (c.moveToFirst()) {  //Nos aseguramos de que existe al menos un registro
                    Toast.makeText(mcontext, "Ya existe un usuario logueado", Toast.LENGTH_SHORT).show();
                }
                else {
                    db.execSQL("INSERT INTO Logins (Usuario_actual) " + "VALUES ('"+email +"')");
                    Toast.makeText(mcontext, "Login exitoso", Toast.LENGTH_SHORT).show();
                }

            }
            //luego al final fuera del for cuando termine
            finally {
                db.setTransactionSuccessful();
            }
            db.endTransaction();
            db.close();
        }
    }
    //CLASE que extiende de AsyncTask para que en segundo plano conecte con el servidor y localice posicion
    public class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0], urls[1],urls[2]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            result="ok";
            if(result.equals("ok")){
                insertlogin(mcontext, usermail.getText().toString());
                Login_correcto();
            }
            else{
                Toast.makeText(mcontext, "Datos inválidos, Verifiquelos", Toast.LENGTH_LONG).show();
            }
        }
    }//Asynctask

    public void Login_correcto(){
        Intent intvalid = new Intent(this,menu.class);
        startActivity(intvalid);
    }
    public void Paso_registro(){
        Intent a= new Intent(this,Registro.class);
        startActivity(a);
    }
}



