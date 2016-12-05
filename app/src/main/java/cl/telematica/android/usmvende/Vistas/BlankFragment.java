package cl.telematica.android.usmvende.Vistas;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cl.telematica.android.usmvende.Adapters.MyAdapterComprador;
import cl.telematica.android.usmvende.HttpServerConnection;
import cl.telematica.android.usmvende.Models.Producto;
import cl.telematica.android.usmvende.R;

/**
 * Created by simon on 02-12-2016.
 */

public class BlankFragment extends Fragment{

    private RecyclerView rv;

    public BlankFragment() {
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
        //Bundle bundle;
       // Producto producto;// = new Producto();
        //producto = getActivity().getIntent().getParcelableExtra("Data");
        //productoList.add(producto);
        //MyAdapterComprador adapter = new MyAdapterComprador(getActivity() , getListProduct());
        //rv.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute(){

            }

            @Override
            protected String doInBackground(Void... params) {
                String resultado = new HttpServerConnection().connectToServer("http://usmvende.telprojects.xyz/compradorall", 1500);
                return resultado;
            }

            @Override
            protected void onPostExecute(String result) {
                if(result != null){
                    System.out.println(result);

                    // specify an adapter (see also next example)
                    MyAdapterComprador adapter = new MyAdapterComprador(getActivity() , getListProduct(result));
                    //adapter.setSwitch(1);
                    rv.setAdapter(adapter);
                }
            }
        };
        task.execute();
        return rootView;
    }

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
                producto.setDescripcion(objeto.getString("descripion"));
                producto.setSell(objeto.getInt("vendiendo"));

                listaProducto.add(producto);
            }
            return listaProducto;
        } catch (JSONException e) {
            e.printStackTrace();
            return listaProducto;
        }
    }
}
