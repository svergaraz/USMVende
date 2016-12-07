package cl.telematica.android.usmvende.Vistas;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cl.telematica.android.usmvende.Adapters.MyAdapterComprador;
import cl.telematica.android.usmvende.Models.BaseDatosSqlite;
import cl.telematica.android.usmvende.Models.Producto;
import cl.telematica.android.usmvende.R;

/**
 * Created by simon on 02-12-2016.
 */

public class BlankFragment2 extends Fragment{

    private RecyclerView rv;

    public BlankFragment2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_comprador, container, false);

        rv = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        /*
        MyAdapterComprador adapter = new MyAdapterComprador(getActivity() ,getListProductFav());
        rv.setAdapter(adapter);
        */

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                String result = POST("http://usmvende.telprojects.xyz/compradorfav", "\""+consulta(getContext())+"\"");
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                System.out.println(result);
                if (result != null) {
                    System.out.println(result);
                    // specify an adapter (see also next example)
                    MyAdapterComprador adapter = new MyAdapterComprador(getActivity(), getListProduct(result));
                    adapter.setSwitch(1);
                    rv.setAdapter(adapter);
                }
            }
        };
        task.execute();
        return rootView;
    }
    /*
    public List<Producto> getListProductFav(){
        List<Producto> producto = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            Producto  p = new Producto();
            p.setDescripcion("juana de arco "+i+" es muy bonito");
            p.setNombreP("Producto"+ i);
            p.setPrecio("$22"+i);
            producto.add(p);
        }
        return producto;
    }
    */

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

    public List<Producto> getListProduct(String result){
        List<Producto> listaProducto = new ArrayList<Producto>();

        try {
            JSONArray lista = new JSONArray(result);

            int size = lista.length();
            for(int i = 0; i < size; i++){
                Producto producto = new Producto();
                JSONObject objeto = lista.getJSONObject(i);

                producto.setNombreP(objeto.getString("producto"));
                producto.setNombreV(objeto.getString("vendedor"));
                producto.setPrecio(objeto.getString("precio"));
                producto.setDescripcion(objeto.getString("descripcion"));
                producto.setSell(objeto.getString("vendiendo"));
                producto.setFav("1");

                listaProducto.add(producto);
            }
            return listaProducto;
        } catch (JSONException e) {
            e.printStackTrace();
            return listaProducto;
        }
    }
}
