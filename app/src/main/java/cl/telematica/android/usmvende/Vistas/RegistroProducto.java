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


public class RegistroProducto extends AppCompatActivity{

    EditText txtNP, txtDP, txtPP, txtNV;

    /*______________________________________________*/
    private RecyclerView recyclerView;
    private MyAdapterComprador adapter;
    private ArrayList<Producto> listProduct;
    private FloatingActionButton fab;
    /*______________________________________________*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*____________________________________*/
        setContentView(R.layout.act_rg_pro);
        recyclerView = (RecyclerView) findViewById(R.id.recyle_view);
        listProduct = new ArrayList<Producto>();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        adapter = new MyAdapterComprador (this, listProduct);
        recyclerView.setLayoutManager(layoutManager);

        fab.setOnClickListener(onAddingListener());
        /*____________________________________*/
    }

    //Metodo de ciclo vida del activity principal Registrar Producto
    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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
}
