package cl.telematica.android.usmvende.Vistas;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cl.telematica.android.usmvende.Adapters.MyAdapterComprador;
import cl.telematica.android.usmvende.Models.Producto;
import cl.telematica.android.usmvende.R;

/**
 * Created by simon on 02-12-2016.
 */

public class BlankFragment2 extends Fragment{

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

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        MyAdapterComprador adapter = new MyAdapterComprador(getActivity() ,getListProductFav());
        rv.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        return rootView;
    }

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
}
