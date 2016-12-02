package cl.telematica.android.usmvende.Vistas;

import android.content.Context;
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

import cl.telematica.android.usmvende.R;

public class Registro extends AppCompatActivity {
    EditText password,usermail;
    Button register;
    Context mcontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        password = (EditText)findViewById(R.id.passreg);
        usermail = (EditText)findViewById(R.id.emailreg);
        register = (Button) findViewById(R.id.register2);
        mcontext=this;

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpAsyncTask().execute("URL/registro",usermail.getText().toString(),password.getText().toString());
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
                Toast.makeText(mcontext, "Registro realizado con exito", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(mcontext, "Datos inválidos, Verifiquelos", Toast.LENGTH_LONG).show();
            }
        }
    }//Asynctask
}
